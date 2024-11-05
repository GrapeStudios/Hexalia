package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.BrewShelfBlockEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BrewShelfBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty FRONT_VARIANT = IntegerProperty.create("front_variant", 0, 2);

    public BrewShelfBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(FRONT_VARIANT, 0));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, OPEN, FRONT_VARIANT);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof Container) {
                Containers.dropContents(pLevel, pPos, (Container)blockentity);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BrewShelfBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            if (player.getItemInHand(hand).getItem() == ModItems.HEX_FOCUS.get()) {
                int currentVariant = state.getValue(FRONT_VARIANT);
                int nextVariant = (currentVariant + 1) % 3;
                world.setBlock(pos, state.setValue(FRONT_VARIANT, nextVariant), 3);
                if (world instanceof ServerLevel serverWorld) {
                    BlockParticleOption particleEffect = new BlockParticleOption(ParticleTypes.BLOCK, state);
                    serverWorld.sendParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.0);
                }
                world.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            } else if (world.getBlockEntity(pos) instanceof BrewShelfBlockEntity brewShelfBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, brewShelfBlockEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }



    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BrewShelfBlockEntity) {
                ((BrewShelfBlockEntity) blockEntity).setCustomName(pStack.getHoverName());
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockEntity(pPos) instanceof BrewShelfBlockEntity brewShelfBlockEntity) {
            brewShelfBlockEntity.recheckOpen();
        }
    }
}
