package net.grapes.hexalia.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class HexedBulrushBlock extends TallPlantBlock implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public HexedBulrushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        FluidState fluidState = world.getFluidState(blockPos);

        if (canPlaceInWater(world, blockPos) && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(HALF, DoubleBlockHalf.LOWER)
                    .with(WATERLOGGED, fluidState.isOf(Fluids.WATER));
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        DoubleBlockHalf half = state.get(HALF);
        Direction.Axis axis = direction.getAxis();

        if (axis == Direction.Axis.Y) {
            boolean isLowerHalf = (half == DoubleBlockHalf.LOWER);
            if ((direction == Direction.UP && isLowerHalf) || (direction == Direction.DOWN && !isLowerHalf)) {
                return newState.isOf(this) && newState.get(HALF) != half ? state : Blocks.AIR.getDefaultState();
            }
        }

        return state;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockBelow = pos.down();
        BlockState groundState = world.getBlockState(blockBelow);
        FluidState fluidState = world.getFluidState(pos);

        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            return groundState.isOf(this) && groundState.get(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return fluidState.isOf(Fluids.WATER) && this.canPlantOnTop(groundState, world, blockBelow);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isSolidBlock(world, pos);
    }

    private boolean canPlaceInWater(WorldView world, BlockPos pos) {
        return world.getFluidState(pos).isOf(Fluids.WATER);
    }
}
