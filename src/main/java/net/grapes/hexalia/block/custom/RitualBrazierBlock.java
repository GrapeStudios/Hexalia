package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.RitualBrazierBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RitualBrazierBlock extends BaseEntityBlock implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty SALTED = BooleanProperty.create("salted");

    protected static final VoxelShape SHAPE = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0));

    public RitualBrazierBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SALTED, false)); // Default to unsalted
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }


    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        ItemStack heldItem = pPlayer.getItemInHand(pHand);

        if (!(blockEntity instanceof RitualBrazierBlockEntity ritualBrazierBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (pHand == InteractionHand.MAIN_HAND) {
            // Check if the player is holding salt and the block is not already salted
            if (heldItem.is(ModItems.SALT.get()) && !pState.getValue(SALTED)) {
                pLevel.setBlock(pPos, pState.setValue(SALTED, true), Block.UPDATE_ALL);
                if (!pPlayer.isCreative()) {
                    heldItem.shrink(1); // Consume the salt
                }
                pLevel.playSound(null, pPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f); // Play bone meal sound
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }

            // Handle item insertion/removal
            if (!heldItem.isEmpty() && ritualBrazierBlockEntity.isEmpty() && !heldItem.is(ModItems.HEX_FOCUS.get()) && !heldItem.is(ModItems.SALT.get())) {
                if (ritualBrazierBlockEntity.addStack(heldItem.split(1))) {
                    playItemSound(pLevel, pPos);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }

            if (!ritualBrazierBlockEntity.isEmpty() && heldItem.isEmpty()) {
                removeItemFromBlock(pLevel, ritualBrazierBlockEntity, pPlayer);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof RitualBrazierBlockEntity ritualBrazierBlockEntity) {
                Containers.dropContents(pLevel, pPos, ritualBrazierBlockEntity);

                // Drop salt if the block is salted
                if (pState.getValue(SALTED)) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), new ItemStack(ModItems.SALT.get()));
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(SALTED, false); // Default to unsalted
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, SALTED); // Add SALTED property
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    private void removeItemFromBlock(Level pLevel, RitualBrazierBlockEntity ritualBrazierBlockEntity, Player player) {
        ItemStack stack = ritualBrazierBlockEntity.removeStack();
        if (!player.getInventory().add(stack)) {
            Containers.dropItemStack(pLevel, player.getX(), player.getY(), player.getZ(), stack);
        }
        playItemSound(pLevel, ritualBrazierBlockEntity.getBlockPos());
    }

    private void playItemSound(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RitualBrazierBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.RITUAL_BRAZIER_BE.get(), (pLevel1, pPos, pState1, pBlockEntity)
                -> pBlockEntity.tick(pLevel1, pPos, pState1, pBlockEntity));
    }
}