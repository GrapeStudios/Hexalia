package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.RitualBrazierBlockEntity;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.RitualTableRecipe;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
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

    public RitualTableBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualTableBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RitualTableBlockEntity tableBE) {
                Containers.dropContents(level, pos, tableBE);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RitualTableBlockEntity tableBE)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        if (held.isEmpty() && !tableBE.isEmpty()) {
            removeItemFromBlock(level, pos, player, tableBE);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!held.isEmpty() && tableBE.isEmpty() && !held.is(ModItems.HEX_FOCUS.get())) {
            addItemToBlock(held, level, pos, player, tableBE);
            spawnParticles(level, pos, ParticleTypes.POOF, 5, 10);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.is(ModItems.HEX_FOCUS.get())) {
            if (tryStartRitual(level, pos, player, tableBE)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private void addItemToBlock(ItemStack held, Level level, BlockPos pos, Player player, RitualTableBlockEntity table) {
        table.setItem(0, held);
        if (!player.getAbilities().instabuild) held.shrink(1);
        playPickupPlaceSounds(level, pos);
    }

    private void removeItemFromBlock(Level level, BlockPos pos, Player player, RitualTableBlockEntity table) {
        ItemStack fromTable = table.getItem(0);
        if (!fromTable.isEmpty()) {
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(InteractionHand.MAIN_HAND, fromTable);
            } else {
                player.setItemInHand(InteractionHand.MAIN_HAND, fromTable.copy());
            }
            table.clearContent();
            playPickupPlaceSounds(level, pos);
        }
    }

    private boolean tryStartRitual(Level level, BlockPos pos, Player player, RitualTableBlockEntity tableBE) {
        ItemStack tableItem = tableBE.getItem(0);
        if (tableItem.isEmpty()) {
            failWithMessage(level, pos, player, "message.hexalia.ritual.missing_ingredients");
            return true;
        }

        BlockPos[] offsets = {pos.north(2), pos.south(2), pos.east(2), pos.west(2)};
        List<ItemStack> brazierItems = new ArrayList<>();
        List<RitualBrazierBlockEntity> brazierEntities = new ArrayList<>();

        for (BlockPos bPos : offsets) {
            if (level.getBlockEntity(bPos) instanceof RitualBrazierBlockEntity brazier) {
                ItemStack stored = brazier.getStoredItem();
                if (stored.isEmpty()) continue;
                BlockState bs = level.getBlockState(bPos);
                if (!bs.hasProperty(RitualBrazierBlock.SALTED) || !bs.getValue(RitualBrazierBlock.SALTED)) {
                    failWithMessage(level, pos, player, "message.hexalia.ritual.missing_salt");
                    return true;
                }
                brazierItems.add(stored);
                brazierEntities.add(brazier);
            }
        }

        RitualTableRecipe matched = findMatchingRecipe(level, tableItem, brazierItems);
        if (matched == null) {
            failWithMessage(level, pos, player, "message.hexalia.ritual.wrong_recipe");
            return true;
        }

        List<BlockPos> grownCrops = findFullyGrownCrops(level, pos, 8, 8);
        if (grownCrops.size() < 8) {
            failWithMessage(level, pos, player, "message.hexalia.ritual.invalid_crops");
            return true;
        }

        tableBE.startTransformation(
                matched.getResultItem(level.registryAccess()).copy(),
                RitualTableBlockEntity.DURATION,
                brazierEntities,
                grownCrops
        );

        tableBE.setGrownCropPositions(grownCrops);

        for (RitualBrazierBlockEntity brazier : brazierEntities) {
            BlockPos bp = brazier.getBlockPos();
            BlockState s = level.getBlockState(bp);
            if (s.hasProperty(RitualBrazierBlock.SALTED) && s.getValue(RitualBrazierBlock.SALTED)) {
                level.setBlock(bp, s.setValue(RitualBrazierBlock.SALTED, false), Block.UPDATE_ALL);
            }
        }

        playPickupPlaceSounds(level, pos);
        spawnParticles(level, pos, ParticleTypes.POOF, 5, 10);
        return true;
    }

    @Nullable
    private RitualTableRecipe findMatchingRecipe(Level level, ItemStack tableItem, List<ItemStack> brazierItems) {
        List<RitualTableRecipe> candidates = level.getRecipeManager().getAllRecipesFor(RitualTableRecipe.Type.INSTANCE);
        for (RitualTableRecipe recipe : candidates) {
            var ings = recipe.getIngredients();
            if (ings.isEmpty()) continue;
            if (!ings.get(0).test(tableItem)) continue;
            List<Ingredient> required = ings.subList(1, ings.size());
            if (containsAll(required, brazierItems)) return recipe;
        }
        return null;
    }

    private boolean containsAll(List<Ingredient> required, List<ItemStack> provided) {
        List<ItemStack> pool = new ArrayList<>(provided);
        for (Ingredient need : required) {
            int idx = indexOfMatch(pool, need);
            if (idx < 0) return false;
            pool.remove(idx);
        }
        return true;
    }

    private int indexOfMatch(List<ItemStack> stacks, Ingredient ing) {
        for (int i = 0; i < stacks.size(); i++) {
            if (ing.test(stacks.get(i))) return i;
        }
        return -1;
    }

    private List<BlockPos> findFullyGrownCrops(Level level, BlockPos center, int requiredCount, int radius) {
        List<BlockPos> found = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                BlockState s = level.getBlockState(p);
                if (s.getBlock() instanceof CropBlock crop && crop.isMaxAge(s)) {
                    found.add(p);
                    if (found.size() >= requiredCount) return found;
                }
            }
        }
        return found;
    }

    private void failWithMessage(Level level, BlockPos pos, Player player, String key) {
        spawnParticles(level, pos, ParticleTypes.SMOKE, 8, 12);
        if (!level.isClientSide) player.displayClientMessage(Component.translatable(key), true);
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.6f);
    }

    private void spawnParticles(Level level, BlockPos pos, SimpleParticleType type, int min, int max) {
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

    private void playPickupPlaceSounds(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundSource.BLOCKS, 0.8f, 0.5f);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == ModBlockEntityTypes.RITUAL_TABLE_BE.get()
                ? (lvl, p, st, be) -> RitualTableBlockEntity.serverTick(lvl, p, st, (RitualTableBlockEntity) be)
                : null;
    }
}