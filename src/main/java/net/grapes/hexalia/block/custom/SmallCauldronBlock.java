package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.HeatingBlockEntity;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.block.entity.SmallCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronBlock extends BaseEntityBlock implements HeatingBlockEntity {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Shapes.or(Block.box(2.0, 0, 2.0, 14.0, 11.0, 14.0),
            Block.box(3.0, 1.0, 3.0, 13.0, 12.0, 13.0));
    public SmallCauldronBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockPos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        FluidState fluidState = level.getFluidState(blockPos);
        return defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        boolean isHeated = isHeated(pLevel, pPos);
        if (isHeated) {
            createBrewingParticles(pLevel, pPos, pRandom, 3);
        }
    }

    private void createBrewingParticles(Level pLevel, BlockPos pPos, RandomSource pRandom, int particleFrequency) {
        if (particleFrequency > 0 && pRandom.nextInt(5) < particleFrequency) {
            for (int i = 0; i < pRandom.nextInt(1) + 1; i++) {
                pLevel.addParticle(ParticleTypes.BUBBLE_POP,
                        (double)pPos.getX() + 0.5,
                        (double)pPos.getY() + 1.0,
                        (double)pPos.getZ() + 0.5,
                        pRandom.nextGaussian() * 0.02,
                        pRandom.nextGaussian() * 0.05 + 0.05,
                        pRandom.nextGaussian() * 0.02);
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SmallCauldronBlockEntity) {
                ((SmallCauldronBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof SmallCauldronBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)pPlayer), (SmallCauldronBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SmallCauldronBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.SMALL_CAULDRON_BE.get(), (pLevel1, pPos, pState1, pBlockEntity)
                -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}
