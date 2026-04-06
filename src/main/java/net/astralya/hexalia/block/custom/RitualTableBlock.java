package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipeInput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CropBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ItemScatterer;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125),
            VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
            VoxelShapes.cuboid(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
            VoxelShapes.cuboid(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
    );

    private static final class Match {
        final RitualTableRecipe recipe;
        final List<RitualBrazierBlockEntity> usedBraziers;
        Match(RitualTableRecipe r, List<RitualBrazierBlockEntity> u) { recipe = r; usedBraziers = u; }
    }

    public static final MapCodec<RitualTableBlock> CODEC = createCodec(RitualTableBlock::new);

    public RitualTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> b) { b.add(FACING); }

    @Override
    public VoxelShape getOutlineShape(BlockState s, BlockView w, BlockPos p, ShapeContext c) { return SHAPE; }

    @Override
    public BlockRenderType getRenderType(BlockState s) { return BlockRenderType.MODEL; }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new RitualTableBlockEntity(pos, state); }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE) {
                ItemScatterer.spawn(world, pos, tableBE);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(world.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE))
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (stack.isOf(ModItems.HEX_FOCUS) && tryStartRitual(world, pos, player, tableBE)) {
            return ItemActionResult.SUCCESS;
        }
        if (tableBE.isEmpty() && !stack.isEmpty() && !stack.isOf(ModItems.HEX_FOCUS)) {
            addItemToBlock(stack, world, pos, player, tableBE);
            return ItemActionResult.SUCCESS;
        }
        if (stack.isEmpty() && !tableBE.getStack(0).isEmpty()) {
            removeItemFromBlock(world, pos, player, tableBE);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean tryStartRitual(World world, BlockPos pos, PlayerEntity player, RitualTableBlockEntity tableBE) {
        ItemStack tableItem = tableBE.getStack(0);
        if (tableItem.isEmpty()) {
            fail(world, pos, player, "message.hexalia.ritual.missing_ingredients");
            return true;
        }
        BlockPos[] offsets = { pos.north(2), pos.south(2), pos.east(2), pos.west(2) };
        List<RitualBrazierBlockEntity> filled = new ArrayList<>();
        for (BlockPos bp : offsets) {
            if (world.getBlockEntity(bp) instanceof RitualBrazierBlockEntity b && !b.getStoredItem().isEmpty()) {
                filled.add(b);
            }
        }
        Match match = findMatch(world, tableItem, filled, tableBE);
        if (match == null) {
            fail(world, pos, player, "message.hexalia.ritual.wrong_recipe");
            return true;
        }
        for (RitualBrazierBlockEntity b : match.usedBraziers) {
            BlockState bs = world.getBlockState(b.getPos());
            if (!bs.contains(RitualBrazierBlock.SALTED) || !bs.get(RitualBrazierBlock.SALTED)) {
                fail(world, pos, player, "message.hexalia.ritual.missing_salt");
                return true;
            }
        }
        List<BlockPos> grownCrops = findFullyGrownCrops(world, pos, 8, 8);
        if (grownCrops.size() < 8) {
            fail(world, pos, player, "message.hexalia.ritual.invalid_crops");
            return true;
        }
        int duration = match.usedBraziers.size() * 40;
        tableBE.startTransformation(
                match.recipe.getResult(world.getRegistryManager()).copy(),
                duration,
                match.usedBraziers
        );
        tableBE.setGrownCropPositions(grownCrops);
        play(world, pos);
        puff(world, pos, ParticleTypes.POOF, 5, 10);
        return true;
    }

    private @Nullable Match findMatch(World world, ItemStack tableItem,
                                      List<RitualBrazierBlockEntity> available, RitualTableBlockEntity tableBE) {
        RitualTableRecipeInput input = new RitualTableRecipeInput(tableBE.getStack(0));
        List<RecipeEntry<RitualTableRecipe>> candidates =
                world.getRecipeManager().getAllMatches(ModRecipes.RITUAL_TABLE_TYPE, input, world);
        for (RecipeEntry<RitualTableRecipe> entry : candidates) {
            RitualTableRecipe r = entry.value();
            var ings = r.getIngredients();
            if (ings.isEmpty() || !ings.get(0).test(tableItem)) continue;
            List<RitualBrazierBlockEntity> pool = new ArrayList<>(available);
            List<RitualBrazierBlockEntity> used = new ArrayList<>();
            boolean ok = true;
            for (Ingredient need : ings.subList(1, ings.size())) {
                int idx = -1;
                for (int i = 0; i < pool.size(); i++) {
                    if (need.test(pool.get(i).getStoredItem())) { idx = i; break; }
                }
                if (idx == -1) { ok = false; break; }
                used.add(pool.remove(idx));
            }
            if (ok) return new Match(r, used);
        }
        return null;
    }

    private List<BlockPos> findFullyGrownCrops(World world, BlockPos center, int required, int radius) {
        List<BlockPos> found = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos p = center.add(dx, 0, dz);
                BlockState s = world.getBlockState(p);
                if (s.getBlock() instanceof CropBlock crop && crop.isMature(s)) {
                    found.add(p);
                    if (found.size() >= required) return found;
                }
            }
        }
        return found;
    }

    private void addItemToBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player, RitualTableBlockEntity be) {
        be.setStack(0, stack);
        if (!player.getAbilities().creativeMode) stack.decrement(1);
        play(world, pos);
    }

    private void removeItemFromBlock(World world, BlockPos pos, PlayerEntity player, RitualTableBlockEntity be) {
        ItemStack onTable = be.getStack(0);
        if (!player.getAbilities().creativeMode) {
            player.setStackInHand(Hand.MAIN_HAND, onTable);
        }
        be.clear();
        play(world, pos);
    }

    private void fail(World world, BlockPos pos, PlayerEntity player, String key) {
        puff(world, pos, ParticleTypes.SMOKE, 8, 12);
        if (!world.isClient) player.sendMessage(Text.translatable(key), true);
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 0.6f);
    }

    private void puff(World world, BlockPos pos, SimpleParticleType type, int min, int max) {
        if (world instanceof ServerWorld server) {
            int count = ThreadLocalRandom.current().nextInt(min, max);
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0 + ThreadLocalRandom.current().nextDouble(0.0, 0.5);
                double z = pos.getZ() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                server.spawnParticles(type, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void play(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundCategory.BLOCKS, 0.8f, 0.5f);
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8f, 0.5f);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return validateTicker(type, ModBlockEntityTypes.RITUAL_TABLE,
                RitualTableBlockEntity::serverTick);
    }
}