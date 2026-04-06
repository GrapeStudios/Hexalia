package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125),
            Shapes.box(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
            Shapes.box(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
            Shapes.box(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
    );

    private static final class Match {
        final RitualTableRecipe recipe;
        final List<RitualBrazierBlockEntity> usedBraziers;
        Match(RitualTableRecipe r, List<RitualBrazierBlockEntity> u){ recipe = r; usedBraziers = u; }
    }

    public static final MapCodec<RitualTableBlock> CODEC = simpleCodec(RitualTableBlock::new);

    public RitualTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) { return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) { b.add(FACING); }
    @Override protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) { return SHAPE; }
    @Override protected RenderShape getRenderShape(BlockState s) { return RenderShape.MODEL; }
    @Override public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new RitualTableBlockEntity(pos, state); }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE) {
                Containers.dropContents(level, pos, tableBE);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(level.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (stack.is(ModItems.HEX_FOCUS) && tryStartRitual(level, pos, player, tableBE)) {
            return ItemInteractionResult.SUCCESS;
        }

        if (tableBE.isEmpty() && !stack.isEmpty() && !stack.is(ModItems.HEX_FOCUS)) {
            addItemToBlock(stack, level, pos, player, tableBE);
            return ItemInteractionResult.SUCCESS;
        }

        if (stack.isEmpty() && !tableBE.getItem(0).isEmpty()) {
            removeItemFromBlock(level, pos, player, tableBE);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean tryStartRitual(Level level, BlockPos pos, Player player, RitualTableBlockEntity tableBE) {
        ItemStack tableItem = tableBE.getItem(0);
        if (tableItem.isEmpty()) {
            fail(level, pos, player, "message.hexalia.ritual.missing_ingredients");
            return true;
        }

        BlockPos[] offsets = { pos.north(2), pos.south(2), pos.east(2), pos.west(2) };
        List<RitualBrazierBlockEntity> filled = new ArrayList<>();
        for (BlockPos bp : offsets) {
            if (level.getBlockEntity(bp) instanceof RitualBrazierBlockEntity b && !b.getStoredItem().isEmpty()) {
                filled.add(b);
            }
        }

        Match match = findMatch(level, tableItem, filled, tableBE);
        if (match == null) {
            fail(level, pos, player, "message.hexalia.ritual.wrong_recipe");
            return true;
        }

        for (RitualBrazierBlockEntity b : match.usedBraziers) {
            BlockState bs = level.getBlockState(b.getBlockPos());
            if (!bs.hasProperty(RitualBrazierBlock.SALTED) || !bs.getValue(RitualBrazierBlock.SALTED)) {
                fail(level, pos, player, "message.hexalia.ritual.missing_salt");
                return true;
            }
        }

        List<BlockPos> grownCrops = findFullyGrownCrops(level, pos, 8, 8);
        if (grownCrops.size() < 8) {
            fail(level, pos, player, "message.hexalia.ritual.invalid_crops");
            return true;
        }

        int duration = match.usedBraziers.size() * 40;

        tableBE.startTransformation(
                match.recipe.getResultItem(level.registryAccess()).copy(),
                duration,
                match.usedBraziers
        );
        tableBE.setGrownCropPositions(grownCrops);

        play(level, pos);
        puff(level, pos, ParticleTypes.POOF, 5, 10);
        return true;
    }

    private @Nullable Match findMatch(Level level, ItemStack tableItem,
                                      List<RitualBrazierBlockEntity> available, RitualTableBlockEntity tableBE) {
        RitualTableRecipeInput input = new RitualTableRecipeInput(tableBE);
        List<RecipeHolder<RitualTableRecipe>> candidates =
                level.getRecipeManager().getRecipesFor(ModRecipes.RITUAL_TABLE_TYPE.get(), input, level);

        for (RecipeHolder<RitualTableRecipe> holder : candidates) {
            RitualTableRecipe r = holder.value();
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

    private List<BlockPos> findFullyGrownCrops(Level level, BlockPos center, int required, int radius) {
        List<BlockPos> found = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                BlockState s = level.getBlockState(p);
                if (s.getBlock() instanceof CropBlock crop && crop.isMaxAge(s)) {
                    found.add(p);
                    if (found.size() >= required) return found;
                }
            }
        }
        return found;
    }

    private void addItemToBlock(ItemStack stack, Level level, BlockPos pos, Player player, RitualTableBlockEntity be) {
        be.setItem(0, stack);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        play(level, pos);
    }

    private void removeItemFromBlock(Level level, BlockPos pos, Player player, RitualTableBlockEntity be) {
        ItemStack onTable = be.getItem(0);
        if (!player.getAbilities().instabuild) {
            player.setItemInHand(InteractionHand.MAIN_HAND, onTable);
        }
        be.clearContent();
        play(level, pos);
    }

    private void fail(Level level, BlockPos pos, Player player, String key) {
        puff(level, pos, ParticleTypes.SMOKE, 8, 12);
        if (!level.isClientSide) player.displayClientMessage(Component.translatable(key), true);
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.6f);
    }

    private void puff(Level level, BlockPos pos, SimpleParticleType type, int min, int max) {
        if (level instanceof ServerLevel server) {
            int count = ThreadLocalRandom.current().nextInt(min, max);
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0 + ThreadLocalRandom.current().nextDouble(0.0, 0.5);
                double z = pos.getZ() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                server.sendParticles(type, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void play(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundSource.BLOCKS, 0.8f, 0.5f);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == net.astralya.hexalia.block.entity.ModBlockEntityTypes.RITUAL_TABLE.get()
                ? (lvl, p, st, be) -> RitualTableBlockEntity.serverTick(lvl, p, st, (RitualTableBlockEntity) be)
                : null;
    }
}
