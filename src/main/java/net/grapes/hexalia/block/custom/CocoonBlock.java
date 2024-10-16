package net.grapes.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CocoonBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);
    private static final int HATCH_DELAY_TICKS = 60;

    private static final VoxelShape NORTH_SHAPE = Shapes.create(new AABB(5.0 / 16.0, 5.0 / 16.0, 11.0 / 16.0, 11.0 / 16.0, 12.0 / 16.0, 1.0));
    private static final VoxelShape SOUTH_SHAPE = Shapes.create(new AABB(5.0 / 16.0, 5.0 / 16.0, 0.0, 11.0 / 16.0, 12.0 / 16.0, 5.0 / 16.0));
    private static final VoxelShape WEST_SHAPE = Shapes.create(new AABB(11.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 1.0, 12.0 / 16.0, 11.0 / 16.0));
    private static final VoxelShape EAST_SHAPE = Shapes.create(new AABB(0.0, 5.0 / 16.0, 5.0 / 16.0, 5.0 / 16.0, 12.0 / 16.0, 11.0 / 16.0));


    public CocoonBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HATCH, 0));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        return switch (direction) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection.getOpposite() == pState.getValue(FACING) && !pState.canSurvive(pLevel, pPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction facing = pState.getValue(FACING);
        BlockPos oppositePos = pPos.relative(facing.getOpposite());
        BlockState oppositeState = pLevel.getBlockState(oppositePos);
        return oppositeState.is(BlockTags.LOGS_THAT_BURN);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HATCH);
    }
}
