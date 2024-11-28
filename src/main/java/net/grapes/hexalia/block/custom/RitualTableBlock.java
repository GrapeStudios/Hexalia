package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.grapes.hexalia.block.entity.SaltBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.particle.ModParticles;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RitualTableBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = createShape();

    public RitualTableBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape() {
        return Shapes.or(
                Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125),
                Shapes.box(0.25, 0.125, 0.25, 0.75, 0.625, 0.75),
                Shapes.box(0.1875, 0.625, 0.1875, 0.8125, 0.6875, 0.8125),
                Shapes.box(0.125, 0.6875, 0.125, 0.875, 0.8125, 0.875)
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RitualTableBlockEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RitualTableBlockEntity ritualTable) {
                ItemStack stack = ritualTable.getItem(0);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, stack);
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

        if (!(blockEntity instanceof RitualTableBlockEntity ritualTableBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (heldItem.getItem().equals(ModItems.HEX_FOCUS.get())) {
            if (performTransmutation(ritualTableBlockEntity, pLevel, pPos)) {
                spawnParticleEffect(pLevel, pPos, ParticleTypes.ENCHANT);
                spawnParticleEffect(pLevel, pPos, ModParticles.LEAVES_PARTICLE.get());
                playRitualSound(pLevel, ritualTableBlockEntity.getBlockPos());
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                spawnParticleEffect(pLevel, pPos, ParticleTypes.SMOKE);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        } else {
            return handleItemInteraction(pLevel, ritualTableBlockEntity, pPlayer, pHand);
        }
    }

    private InteractionResult handleItemInteraction(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player, InteractionHand pHand) {
        if (ritualTableBlockEntity.isEmpty()) {
            return addItemFromHand(pLevel, ritualTableBlockEntity, player, pHand);
        } else if (pHand.equals(InteractionHand.MAIN_HAND)) {
            removeItemFromBlock(pLevel, ritualTableBlockEntity, player);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult addItemFromHand(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player, InteractionHand pHand) {
        ItemStack heldItem = player.getItemInHand(pHand);
        ItemStack offHandItem = player.getOffhandItem();

        if (!offHandItem.isEmpty() && pHand.equals(InteractionHand.OFF_HAND)) {
            return InteractionResult.PASS;
        }
        if (heldItem.isEmpty()) {
            return InteractionResult.PASS;
        } else if (ritualTableBlockEntity.addStack(player.getAbilities().instabuild ? heldItem.copy() : heldItem)) {
            playItemSound(pLevel, ritualTableBlockEntity.getBlockPos());
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private void removeItemFromBlock(Level pLevel, RitualTableBlockEntity ritualTableBlockEntity, Player player) {
        BlockPos pos = ritualTableBlockEntity.getBlockPos();
        if (player.getAbilities().instabuild) {
            ritualTableBlockEntity.removeStack();
        } else {
            ItemStack stack = ritualTableBlockEntity.removeStack();
            if (!player.getInventory().add(stack)) {
                Containers.dropItemStack(pLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
        playItemSound(pLevel, pos);
    }

    private boolean performTransmutation(RitualTableBlockEntity ritualTable, Level world, BlockPos pos) {
        SimpleContainer inventory = new SimpleContainer(ritualTable.getContainerSize());
        ItemStack inputStack = ritualTable.getItem(0);

        if (inputStack.isEmpty()) {
            return false;
        }

        inventory.setItem(0, inputStack);
        Optional<TransmutationRecipe> recipeOptional = world.getRecipeManager().getRecipeFor(
                TransmutationRecipe.Type.INSTANCE, ritualTable, world);

        if (recipeOptional.isEmpty()) {
            return false;
        }

        TransmutationRecipe recipe = recipeOptional.get();
        if (checkSaltBlocks(world, pos, recipe)) {
            consumeSaltBlocks(world, pos, recipe);
            ritualTable.removeItem(0, 1);
            ritualTable.setItem(0, recipe.getResultItem(world.registryAccess()).copy());
            ritualTable.setChanged();
            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), Block.UPDATE_ALL);
            return true;
        }
        return false;
    }

    private boolean checkSaltBlocks(Level pLevel, BlockPos tablePos, TransmutationRecipe pRecipe) {
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        NonNullList<ItemStack> requiredSaltItems = NonNullList.create();
        requiredSaltItems.addAll(pRecipe.getSaltItems());

        for (Direction direction : directions) {
            BlockPos saltPos = tablePos.relative(direction, 2);
            BlockEntity saltEntity = pLevel.getBlockEntity(saltPos);

            if (saltEntity instanceof SaltBlockEntity saltBlock) {
                Iterator<ItemStack> iterator = requiredSaltItems.iterator();
                while (iterator.hasNext()) {
                    if (ItemStack.isSameItemSameTags(saltBlock.getItem(0), iterator.next())) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        return requiredSaltItems.isEmpty();
    }

    private void consumeSaltBlocks(Level pLevel, BlockPos tablePos, TransmutationRecipe pRecipe) {
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        NonNullList<ItemStack> requiredSaltItems = NonNullList.create();
        requiredSaltItems.addAll(pRecipe.getSaltItems());

        for (Direction direction : directions) {
            BlockPos saltPos = tablePos.relative(direction, 2);
            BlockEntity saltEntity = pLevel.getBlockEntity(saltPos);

            if (saltEntity instanceof SaltBlockEntity saltBlock) {
                Iterator<ItemStack> iterator = requiredSaltItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack item = iterator.next();
                    if (ItemStack.isSameItemSameTags(saltBlock.getItem(0), item)) {
                        iterator.remove();
                        saltBlock.removeStack();
                        saltBlock.setChanged();
                        pLevel.sendBlockUpdated(saltPos, pLevel.getBlockState(saltPos), pLevel.getBlockState(saltPos), Block.UPDATE_ALL);
                        break;
                    }
                }
            }
        }
    }

    private void spawnParticleEffect(Level pLevel, BlockPos pPos, SimpleParticleType particleType) {
        int particleCount = ThreadLocalRandom.current().nextInt(20, 30); // Randomized particle count for visual variety
        for (int i = 0; i < particleCount; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 0.5);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-0.5, 0.5);
            pLevel.addParticle(particleType, pPos.getX() + 0.5 + offsetX, pPos.getY() + 1.0 + offsetY, pPos.getZ() + 0.5 + offsetZ, 0, 0, 0);
        }
    }

    private void playItemSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 0.5f);
    }

    private void playRitualSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, ModSounds.RITUAL_SUCCESS.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}
