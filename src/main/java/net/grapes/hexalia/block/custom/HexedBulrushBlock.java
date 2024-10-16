package net.grapes.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class HexedBulrushBlock extends TallFlowerBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HexedBulrushBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockPos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        FluidState fluidState = level.getFluidState(blockPos);

        if (canPlaceInWater(level, blockPos) && level.getBlockState(blockPos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        DoubleBlockHalf half = state.getValue(HALF);
        Direction.Axis axis = direction.getAxis();

        if (axis == Direction.Axis.Y) {
            boolean isLowerHalf = (half == DoubleBlockHalf.LOWER);
            if ((direction == Direction.UP && isLowerHalf) || (direction == Direction.DOWN && !isLowerHalf)) {
                return newState.is(this) && newState.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockBelow = pPos.below();
        BlockState groundState = pLevel.getBlockState(blockBelow);
        FluidState fluidState = pLevel.getFluidState(pPos);

        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return groundState.is(this) && groundState.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return fluidState.is(Fluids.WATER) && this.mayPlaceOn(groundState, pLevel, blockBelow);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState pState, Fluid pFluid) {
        return false;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.isSolidRender(world, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, WATERLOGGED);
    }

    private boolean canPlaceInWater(Level pLevel, BlockPos pPos) {
        return pLevel.getFluidState(pPos).is(Fluids.WATER);
    }
}
