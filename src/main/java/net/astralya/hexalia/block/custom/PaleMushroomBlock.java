package net.astralya.hexalia.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class PaleMushroomBlock extends ShroomBlock implements Fertilizable {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty AMOUNT = Properties.FLOWER_AMOUNT;

    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES = Util.memoize((facing, amount) -> {
        VoxelShape[] quads = new VoxelShape[] {
                Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 3.0, 16.0),
                Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 3.0, 8.0),
                Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 3.0, 8.0),
                Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)
        };
        VoxelShape shape = VoxelShapes.empty();
        for (int i = 0; i < amount; i++) {
            int idx = Math.floorMod(i - facing.getHorizontal(), 4);
            shape = VoxelShapes.union(shape, quads[idx]);
        }
        return shape;
    });

    public PaleMushroomBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(AMOUNT, 1));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(AMOUNT) < 4
                || super.canReplace(state, context);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_PROPERTIES.apply(state.get(FACING), state.get(AMOUNT));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState existing = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (existing.isOf(this)) {
            return existing.with(AMOUNT, Math.min(4, existing.get(AMOUNT) + 1));
        }
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, AMOUNT);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int count = state.get(AMOUNT);
        if (count < 4) {
            world.setBlockState(pos, state.with(AMOUNT, count + 1), Block.NOTIFY_LISTENERS);
        } else {
            dropStack(world, pos, new ItemStack(this));
        }
    }
}
