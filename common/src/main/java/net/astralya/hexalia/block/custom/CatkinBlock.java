package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CatkinBlock extends Block {
  public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
  private static final VoxelShape SHAPE = Shapes.box(0.0, 0.875, 0.0, 1.0, 1.0625, 1.0);

  public CatkinBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(HANGING, true));
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return level.getBlockState(pos.above()).is(ModBlocks.COTTONWOOD_LEAVES.get());
  }

  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    return state.canSurvive(level, pos)
        ? super.updateShape(state, direction, neighborState, level, pos, neighborPos)
        : Blocks.AIR.defaultBlockState();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HANGING);
  }
}
