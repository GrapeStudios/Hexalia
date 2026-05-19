package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaltsproutBlock extends BushBlock implements BonemealableBlock {
  public static final MapCodec<SaltsproutBlock> CODEC = simpleCodec(SaltsproutBlock::new);
  public static final int MAX_AGE = 2;
  public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

  private static final VoxelShape SHAPE = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0));

  public SaltsproutBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(AGE, 0));
  }

  @Override
  protected MapCodec<? extends BushBlock> codec() {
    return CODEC;
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.BEE) {
      return;
    }
    if (!level.isClientSide() && state.getValue(AGE) == MAX_AGE) {
      double deltaX = Math.abs(entity.getX() - entity.xOld);
      double deltaZ = Math.abs(entity.getZ() - entity.zOld);
      if (deltaX >= 0.003F || deltaZ >= 0.003F) {
        entity.hurt(level.damageSources().cactus(), 0.5F);
      }
    }
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    int age = state.getValue(AGE);
    if (age > 1) {
      int count = 1 + level.random.nextInt(2) + (age == MAX_AGE ? 1 : 0);
      popResource(level, pos, new ItemStack(ModItems.SALTSPROUT.get(), count));
      level.playSound(
          null,
          pos,
          SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
          SoundSource.BLOCKS,
          1.0F,
          0.8F + level.random.nextFloat() * 0.4F);
      BlockState harvested = state.setValue(AGE, 1);
      level.setBlock(pos, harvested, 2);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvested));
      return InteractionResult.sidedSuccess(level.isClientSide());
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hitResult) {
    return state.getValue(AGE) != MAX_AGE && stack.is(Items.BONE_MEAL)
        ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return state.getValue(AGE) < MAX_AGE;
  }

  @Override
  public boolean isBonemealSuccess(
      Level level, RandomSource random, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(
      ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    level.setBlock(pos, state.setValue(AGE, Math.min(MAX_AGE, state.getValue(AGE) + 1)), 2);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      BlockPos sidePos = pos.relative(direction);
      BlockState sideState = level.getBlockState(sidePos);
      if (Block.isShapeFullBlock(sideState.getCollisionShape(level, sidePos))
          || level.getFluidState(sidePos).is(FluidTags.LAVA)) {
        return false;
      }
    }

    BlockState below = level.getBlockState(pos.below());
    return (below.is(ModBlocks.INFUSED_FARMLAND.get()) || below.is(BlockTags.SAND))
        && level.getFluidState(pos.above()).isEmpty();
  }

  @Override
  protected boolean isRandomlyTicking(BlockState state) {
    return state.getValue(AGE) < MAX_AGE;
  }

  @Override
  protected void randomTick(
      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (state.getValue(AGE) < MAX_AGE
        && random.nextInt(5) == 0
        && level.getRawBrightness(pos.above(), 0) >= 9) {
      BlockState grown = state.setValue(AGE, state.getValue(AGE) + 1);
      level.setBlock(pos, grown, 2);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grown));
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(AGE);
  }
}
