package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GaleberriesVinePlantBlock extends GrowingPlantBodyBlock
    implements BonemealableBlock, CaveVines {
  public static final MapCodec<GaleberriesVinePlantBlock> CODEC =
      simpleCodec(GaleberriesVinePlantBlock::new);
  public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;

  private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

  public GaleberriesVinePlantBlock(Properties properties) {
    super(properties, Direction.DOWN, SHAPE, false);
    registerDefaultState(stateDefinition.any().setValue(BERRIES, false));
  }

  @Override
  protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
    return CODEC;
  }

  @Override
  protected GrowingPlantHeadBlock getHeadBlock() {
    return (GrowingPlantHeadBlock) ModBlocks.GALEBERRIES_VINE.get();
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (state.getValue(BERRIES)) {
      if (!level.isClientSide()) {
        popResource(level, pos, new ItemStack(ModItems.GALEBERRIES.get()));
        level.setBlock(pos, state.setValue(BERRIES, false), 2);
      }
      return InteractionResult.sidedSuccess(level.isClientSide());
    }
    return InteractionResult.PASS;
  }

  @Override
  protected BlockState updateHeadAfterConvertedFromBody(BlockState body, BlockState head) {
    return head.setValue(BERRIES, body.getValue(BERRIES));
  }

  @Override
  protected boolean isRandomlyTicking(BlockState state) {
    return !state.getValue(BERRIES);
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!state.getValue(BERRIES) && random.nextFloat() < 0.11F) {
      level.setBlock(pos, state.setValue(BERRIES, true), 2);
    }
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return !state.getValue(BERRIES);
  }

  @Override
  public boolean isBonemealSuccess(
      Level level, RandomSource random, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(
      ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    level.setBlock(pos, state.setValue(BERRIES, true), 2);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BERRIES);
  }
}
