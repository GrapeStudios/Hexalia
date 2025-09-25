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

    public static final MapCodec<RitualTableBlock> CODEC = simpleCodec(RitualTableBlock::new);

    public RitualTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualTableBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE) {
                Containers.dropContents(level, pos, tableBE);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.getBlockEntity(pos) instanceof RitualTableBlockEntity tableBE) {
            if (stack.is(ModItems.HEX_FOCUS) && tryStartRitual(level, pos, player, tableBE, stack)) {
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
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean tryStartRitual(Level level, BlockPos pos, Player player, RitualTableBlockEntity tableBE, ItemStack held) {
        ItemStack tableItem = tableBE.getItem(0);
        BlockPos[] offsets = {pos.north(2), pos.south(2), pos.east(2), pos.west(2)};
        List<ItemStack> brazierItems = new ArrayList<>();
        List<RitualBrazierBlockEntity> brazierEntities = new ArrayList<>();

        for (BlockPos bPos : offsets) {
            if (level.getBlockEntity(bPos) instanceof RitualBrazierBlockEntity brazier && !brazier.getStoredItem().isEmpty()) {
                BlockState state = level.getBlockState(bPos);
                if (!state.hasProperty(RitualBrazierBlock.SALTED) || !state.getValue(RitualBrazierBlock.SALTED)) {
                    failWithMessage(level, pos, player, "message.hexalia.ritual.missing_salt");
                    return true;
                }
                brazierItems.add(brazier.getStoredItem());
                brazierEntities.add(brazier);
            }
        }

        RitualTableRecipe matchedRecipe = findMatchingRecipe(level, tableItem, brazierItems, tableBE);
        if (matchedRecipe == null) {
            failWithMessage(level, pos, player, "message.hexalia.ritual.wrong_recipe");
            return true;
        }

        List<BlockPos> grownCrops = findFullyGrownCrops(level, pos, 8, 8);
        if (grownCrops.size() < 8) {
            failWithMessage(level, pos, player, "message.hexalia.ritual.invalid_crops");
            return true;
        }

        tableBE.startTransformation(matchedRecipe.getResultItem(level.registryAccess()).copy(),
                RitualTableBlockEntity.DURATION, brazierEntities);
        tableBE.setGrownCropPositions(grownCrops);

        for (RitualBrazierBlockEntity brazier : brazierEntities) {
            BlockPos bp = brazier.getBlockPos();
            BlockState state = level.getBlockState(bp);
            if (state.hasProperty(RitualBrazierBlock.SALTED) && state.getValue(RitualBrazierBlock.SALTED)) {
                level.setBlock(bp, state.setValue(RitualBrazierBlock.SALTED, false), 3);
            }
        }

        playInteractionSound(level, pos);
        spawnParticles(level, pos, ParticleTypes.POOF, 5, 10);
        return true;
    }

    private RitualTableRecipe findMatchingRecipe(Level level, ItemStack tableItem, List<ItemStack> brazierItems,
                                                 RitualTableBlockEntity tableBE) {
        RitualTableRecipeInput input = new RitualTableRecipeInput(tableBE);
        List<RecipeHolder<RitualTableRecipe>> candidates =
                level.getRecipeManager().getRecipesFor(ModRecipes.RITUAL_TABLE_TYPE.get(), input, level);

        for (RecipeHolder<RitualTableRecipe> holder : candidates) {
            RitualTableRecipe recipe = holder.value();
            if (!recipe.getIngredients().get(0).test(tableItem)) continue;

            List<ItemStack> remaining = new ArrayList<>(brazierItems);
            boolean allMatch = true;
            for (Ingredient ing : recipe.getIngredients().subList(1, recipe.getIngredients().size())) {
                boolean found = remaining.removeIf(ing::test);
                if (!found) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch && remaining.isEmpty()) {
                return recipe;
            }
        }
        return null;
    }

    private void failWithMessage(Level level, BlockPos pos, Player player, String messageKey) {
        spawnParticles(level, pos, ParticleTypes.SMOKE, 8, 12);
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(messageKey), true);
        }
    }

    private List<BlockPos> findFullyGrownCrops(Level level, BlockPos center, int requiredCount, int radius) {
        List<BlockPos> found = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos checkPos = center.offset(dx, 0, dz);
                BlockState state = level.getBlockState(checkPos);
                if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                    found.add(checkPos);
                    if (found.size() >= requiredCount) return found;
                }
            }
        }
        return found;
    }

    private void addItemToBlock(ItemStack stack, Level level, BlockPos pos, Player player, RitualTableBlockEntity tableEntity) {
        tableEntity.setItem(0, stack);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        playInteractionSound(level, pos);
    }

    private void removeItemFromBlock(Level level, BlockPos pos, Player player, RitualTableBlockEntity tableEntity) {
        ItemStack stackOnTable = tableEntity.getItem(0);
        if (!player.getAbilities().instabuild) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stackOnTable);
        }
        tableEntity.clearContent();
        playInteractionSound(level, pos);
    }

    private void spawnParticles(Level level, BlockPos pos, SimpleParticleType type, int min, int max) {
        if (level instanceof ServerLevel server) {
            int count = ThreadLocalRandom.current().nextInt(min, max);
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0 + ThreadLocalRandom.current().nextDouble(0.0, 0.5);
                double z = pos.getZ() + 0.5 + ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
                server.sendParticles(type, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private void playInteractionSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED, SoundSource.BLOCKS, 0.8f, 0.5f);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == net.astralya.hexalia.block.entity.ModBlockEntityTypes.RITUAL_TABLE.get()
                ? (lvl, pos, st, be) -> RitualTableBlockEntity.serverTick(lvl, pos, st, (RitualTableBlockEntity) be)
                : null;
    }
}
