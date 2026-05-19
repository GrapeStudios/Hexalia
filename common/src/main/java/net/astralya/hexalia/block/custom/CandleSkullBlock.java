package net.astralya.hexalia.block.custom;

import com.google.common.collect.ImmutableList;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CandleSkullBlock extends Block {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final VoxelShape SHAPE =
      Shapes.or(
          Shapes.box(0.25, 0.0, 0.25, 0.75, 0.5, 0.75),
          Shapes.box(0.4375, 0.453125, 0.4375, 0.5625, 0.640625, 0.5625));
  private static final Iterable<Vec3> PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5, 0.75, 0.5));

  public CandleSkullBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false)
            .setValue(WATERLOGGED, false));
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
        .setValue(LIT, false);
  }

  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    BlockPos supportPos = pos.below();
    return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, Direction.UP);
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
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
    ItemStack itemStack = player.getItemInHand(hand);
    if (itemStack.getItem() instanceof FlintAndSteelItem
        && !state.getValue(LIT)
        && !state.getValue(WATERLOGGED)) {
      lightCandleSkull(state, level, pos, player, hand, itemStack);
      return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
    if (itemStack.isEmpty() && state.getValue(LIT)) {
      extinguishCandleSkull(state, level, pos);
      return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    if (!state.getValue(LIT)) {
      return;
    }
    PARTICLE_OFFSETS.forEach(
        offset ->
            spawnCandleParticles(level, offset.add(pos.getX(), pos.getY(), pos.getZ()), random));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED, LIT);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  private static void lightCandleSkull(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      ItemStack itemStack) {
    level.setBlockAndUpdate(pos, state.setValue(LIT, true));
    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    level.playSound(
        null,
        pos,
        SoundEvents.FLINTANDSTEEL_USE,
        SoundSource.BLOCKS,
        1.0F,
        level.random.nextFloat() * 0.4F + 0.8F);
  }

  private static void extinguishCandleSkull(BlockState state, Level level, BlockPos pos) {
    level.setBlockAndUpdate(pos, state.setValue(LIT, false));
    level.playSound(
        null,
        pos,
        SoundEvents.CANDLE_EXTINGUISH,
        SoundSource.BLOCKS,
        1.0F,
        level.random.nextFloat() * 0.4F + 0.8F);
  }

  private static void spawnCandleParticles(Level level, Vec3 vec3, RandomSource random) {
    float chance = random.nextFloat();
    if (chance < 0.3F) {
      level.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
      if (chance < 0.17F) {
        level.playLocalSound(
            vec3.x + 0.5,
            vec3.y + 0.5,
            vec3.z + 0.5,
            SoundEvents.CANDLE_AMBIENT,
            SoundSource.BLOCKS,
            1.0F + random.nextFloat(),
            random.nextFloat() * 0.7F + 0.3F,
            false);
      }
    }

    BlockPos pos = BlockPos.containing(vec3);
    Block block = level.getBlockState(pos).getBlock();
    if (block == ModBlocks.WITHER_CANDLE_SKULL.get()) {
      level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
    } else {
      level.addParticle(ParticleTypes.FLAME, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
    }
  }
}
