package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class SilkwormCocoonBlock extends Block {

    public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
    public static final IntProperty HATCH = IntProperty.of("hatch", 0, 2);
    private static final int HATCH_DELAY_TICKS = 20;

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0.3125, 0.3125, 0.6875, 0.6875, 0.75, 1);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(0.3125, 0.3125, 0, 0.6875, 0.75, 0.3125);
    private static final VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0.6875, 0.3125, 0.3125, 1, 0.75, 0.6875);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.cuboid(0, 0.3125, 0.3125, 0.3125, 0.75, 0.6875);

    public SilkwormCocoonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HATCH, 0));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, HATCH_DELAY_TICKS);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (isAttractingBlockNearby(world, pos)) {
            int hatchStage = state.get(HATCH);
            if (hatchStage < 2) {
                world.setBlockState(pos, state.with(HATCH, hatchStage + 1), 2);
                world.scheduleBlockTick(pos, this, HATCH_DELAY_TICKS);
            } else {
                hatchSilkMoth(world, pos);
            }
        } else {
            world.scheduleBlockTick(pos, this, HATCH_DELAY_TICKS);
        }
    }

    private boolean isAttractingBlockNearby(ServerWorld world, BlockPos pos) {
        BlockPos.Mutable searchPos = new BlockPos.Mutable();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    searchPos.set(pos, dx, dy, dz);
                    if (world.getBlockState(searchPos).isIn(ModTags.Blocks.ATTRACTS_MOTH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void hatchSilkMoth(World world, BlockPos pos) {
        world.removeBlock(pos, false);
        SilkMothEntity silkMoth = ModEntities.SILK_MOTH.create(world);
        if (silkMoth != null) {
            silkMoth.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
            world.spawnEntity(silkMoth);
        }
        world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return switch (direction) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos oppositePos = pos.offset(facing.getOpposite());
        BlockState oppositeState = world.getBlockState(oppositePos);
        return oppositeState.isOf(Blocks.DARK_OAK_LOG);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HATCH);
    }
}
