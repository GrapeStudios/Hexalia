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
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GaleberriesVineBlock extends GrowingPlantHeadBlock
    implements BonemealableBlock, CaveVines {
  public static final MapCodec<GaleberriesVineBlock> CODEC = simpleCodec(GaleberriesVineBlock::new);
  public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;

  private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

  public GaleberriesVineBlock(BlockBehaviour.Properties properties) {
    super(properties, Direction.DOWN, SHAPE, false, 0.1D);
    registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(BERRIES, false));
  }

  @Override
  protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
    return CODEC;
  }

  @Override
  protected Block getBodyBlock() {
    return ModBlocks.GALEBERRIES_VINE_PLANT.get();
  }

  @Override
  protected BlockState updateBodyAfterConvertedFromHead(BlockState head, BlockState body) {
    return body.setValue(BERRIES, head.getValue(BERRIES));
  }

  @Override
  protected BlockState getGrowIntoState(BlockState state, RandomSource random) {
    return super.getGrowIntoState(state, random).setValue(BERRIES, random.nextFloat() < 0.11F);
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    super.randomTick(state, level, pos, random);
    BlockState current = level.getBlockState(pos);
    if (current.is(this) && !current.getValue(BERRIES) && random.nextFloat() < 0.11F) {
      level.setBlock(pos, current.setValue(BERRIES, true), 2);
    }
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
  protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
    return 1;
  }

  @Override
  protected boolean canGrowInto(BlockState state) {
    return state.isAir();
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BERRIES);
  }
}
