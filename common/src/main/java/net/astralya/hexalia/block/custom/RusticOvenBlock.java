package net.astralya.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class RusticOvenBlock extends Block {
  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  public RusticOvenBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
  }

  @Override
  public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
    if (!entity.fireImmune() && !entity.isSteppingCarefully() && entity instanceof LivingEntity) {
      entity.hurt(level.damageSources().hotFloor(), 1.0F);
    }
    super.stepOn(level, pos, state, entity);
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    double x = (double) pos.getX() + 0.5D;
    double y = pos.getY();
    double z = (double) pos.getZ() + 0.5D;

    if (random.nextDouble() < 0.1D) {
      level.playLocalSound(
          x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
    }

    Direction direction = state.getValue(FACING);
    Direction.Axis axis = direction.getAxis();
    double offset = 0.52D;
    double spread = random.nextDouble() * 0.6D - 0.3D;
    double particleX = axis == Direction.Axis.X ? (double) direction.getStepX() * offset : spread;
    double particleY = random.nextDouble() * 10.0D / 16.0D;
    double particleZ = axis == Direction.Axis.Z ? (double) direction.getStepZ() * offset : spread;

    level.addParticle(
        ParticleTypes.SMOKE, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
    level.addParticle(
        ParticleTypes.FLAME, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected BlockState rotate(BlockState state, Rotation direction) {
    return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }
}
