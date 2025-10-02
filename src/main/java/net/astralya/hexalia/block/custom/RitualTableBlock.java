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
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.1875, 0,      0.1875, 0.8125, 0.125,  0.8125),
            VoxelShapes.cuboid(0.25,   0.125,  0.25,   0.75,   0.625,  0.75),
            VoxelShapes.cuboid(0.1875, 0.625,  0.1875, 0.8125, 0.6875, 0.8125),
            VoxelShapes.cuboid(0.125,  0.6875, 0.125,  0.875,  0.8125, 0.875)
    );
    public static final MapCodec<RitualTableBlock> CODEC = createCodec(RitualTableBlock::new);

    private static final class MatchResult {
        final RitualTableRecipe recipe;
        final List<RitualBrazierBlockEntity> usedBraziers;
        MatchResult(RitualTableRecipe recipe, List<RitualBrazierBlockEntity> usedBraziers) {
            this.recipe = recipe;
            this.usedBraziers = usedBraziers;
        }
    }

    public RitualTableBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualTableBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntityTypes.RITUAL_TABLE, RitualTableBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RitualTableBlockEntity table) {
                ItemScatterer.spawn(world, pos, (Inventory) table);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof RitualTableBlockEntity tableBE)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isOf(ModItems.HEX_FOCUS) && tryStartRitual(world, pos, player, tableBE, stack)) {
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

    private boolean tryStartRitual(World world, BlockPos pos, PlayerEntity player,
                                   RitualTableBlockEntity tableBE, ItemStack held) {
        ItemStack tableItem = tableBE.getStack(0);

        BlockPos[] offsets = { pos.north(2), pos.south(2), pos.east(2), pos.west(2) };
        List<RitualBrazierBlockEntity> filledBraziers = new ArrayList<>();

        for (BlockPos bp : offsets) {
            BlockEntity be = world.getBlockEntity(bp);
            if (be instanceof RitualBrazierBlockEntity brazier && !brazier.isEmpty()) {
                filledBraziers.add(brazier);
            }
        }

        MatchResult match = findMatchingRecipe(world, tableItem, filledBraziers);
        if (match == null) {
            failWithMessage(world, pos, player, "message.hexalia.ritual.wrong_recipe");
            return true;
        }

        for (RitualBrazierBlockEntity brazier : match.usedBraziers) {
            BlockState bs = world.getBlockState(brazier.getPos());
            if (bs.contains(RitualBrazierBlock.SALTED) && !bs.get(RitualBrazierBlock.SALTED)) {
                failWithMessage(world, pos, player, "message.hexalia.ritual.missing_salt");
                return true;
            }
        }

        List<BlockPos> grownCrops = findFullyGrownCrops(world, pos, 8, 8);
        if (grownCrops.size() < 8) {
            failWithMessage(world, pos, player, "message.hexalia.ritual.invalid_crops");
            return true;
        }

        int duration = match.usedBraziers.size() * 40;
        tableBE.startTransformation(match.recipe.getResult(world.getRegistryManager()).copy(),
                duration, match.usedBraziers);
        tableBE.setGrownCropPositions(grownCrops);

        for (RitualBrazierBlockEntity brazier : match.usedBraziers) {
            BlockPos bp = brazier.getPos();
            BlockState bs = world.getBlockState(bp);
            if (bs.contains(RitualBrazierBlock.SALTED) && bs.get(RitualBrazierBlock.SALTED)) {
                world.setBlockState(bp, bs.with(RitualBrazierBlock.SALTED, false), 3);
            }
        }

        playInteractionSound(world, pos);
        spawnParticles(world, pos, ParticleTypes.POOF, 5, 10);
        return true;
    }

    private @Nullable MatchResult findMatchingRecipe(World world, ItemStack tableItem,
                                                     List<RitualBrazierBlockEntity> availableBraziers) {
        RitualTableRecipeInput input = new RitualTableRecipeInput(tableItem);
        List<RecipeEntry<RitualTableRecipe>> candidates =
                world.getRecipeManager().getAllMatches(ModRecipes.RITUAL_TABLE_TYPE, input, world);

        for (RecipeEntry<RitualTableRecipe> entry : candidates) {
            RitualTableRecipe recipe = entry.value();
            var ings = recipe.getIngredients();
            if (ings.isEmpty() || !ings.get(0).test(tableItem)) continue;

            List<Ingredient> needed = ings.subList(1, ings.size());
            List<RitualBrazierBlockEntity> pool = new ArrayList<>(availableBraziers);
            List<RitualBrazierBlockEntity> used = new ArrayList<>();

            boolean ok = true;
            for (Ingredient ing : needed) {
                int idx = -1;
                for (int i = 0; i < pool.size(); i++) {
                    if (ing.test(pool.get(i).getStoredItem())) {
                        idx = i;
                        break;
                    }
                }
                if (idx == -1) { ok = false; break; }
                used.add(pool.remove(idx));
            }

            if (ok) {
                return new MatchResult(recipe, used);
            }
        }
        return null;
    }

    private void failWithMessage(World world, BlockPos pos, PlayerEntity player, String key) {
        spawnParticles(world, pos, ParticleTypes.SMOKE, 8, 12);
        if (!world.isClient) player.sendMessage(Text.translatable(key), true);
    }

    private List<BlockPos> findFullyGrownCrops(World world, BlockPos center, int requiredCount, int radius) {
        List<BlockPos> found = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos check = center.add(dx, 0, dz);
                BlockState st = world.getBlockState(check);
                if (st.getBlock() instanceof CropBlock crop && crop.isMature(st)) {
                    found.add(check);
                    if (found.size() >= requiredCount) return found;
                }
            }
        }
        return found;
    }

    private void addItemToBlock(ItemStack stack, World world, BlockPos pos,
                                PlayerEntity player, RitualTableBlockEntity table) {
        table.setStack(0, stack);
        if (!player.getAbilities().creativeMode) stack.decrement(1);
        playInteractionSound(world, pos);
    }

    private void removeItemFromBlock(World world, BlockPos pos,
                                     PlayerEntity player, RitualTableBlockEntity table) {
        ItemStack onTable = table.getStack(0);
        if (!player.getAbilities().creativeMode) {
            player.setStackInHand(Hand.MAIN_HAND, onTable);
        }
        table.clear();
        playInteractionSound(world, pos);
    }

    private void spawnParticles(World world, BlockPos pos, SimpleParticleType type, int min, int max) {
        if (world instanceof ServerWorld server) {
            int count = ThreadLocalRandom.current().nextInt(min, max);
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0 + ThreadLocalRandom.current().nextDouble(0.0, 0.5);
                double z = pos.getZ() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                server.spawnParticles(type, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private void playInteractionSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundCategory.BLOCKS, 0.8f, 0.5f);
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8f, 0.5f);
    }
}