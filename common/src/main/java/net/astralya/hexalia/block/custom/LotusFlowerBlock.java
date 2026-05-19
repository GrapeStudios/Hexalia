package net.astralya.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class LotusFlowerBlock extends WaterlilyBlock {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  public LotusFlowerBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    if (!canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos())) {
      return null;
    }
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    BlockPos below = pos.below();
    return mayPlaceOn(level.getBlockState(below), level, below);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }
}
