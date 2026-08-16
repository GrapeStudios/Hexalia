package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class CelestialBloomBlock extends HerbBlock {
  public CelestialBloomBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
    super(effect, seconds, properties);
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    return state.is(ModBlocks.CELESTIAL_BLOOM.get())
        && super.isValidBonemealTarget(level, pos, state);
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    boolean withered = state.is(ModBlocks.WITHERED_CELESTIAL_BLOOM.get());
    if (random.nextInt(withered ? 6 : 3) != 0) {
      return;
    }
    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.35;
    double y = pos.getY() + 0.45 + random.nextDouble() * 0.45;
    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.35;
    double velocityScale = withered ? 0.6 : 1.0;
    level.addParticle(
        ModParticleTypes.SPARKLE.get(),
        x,
        y,
        z,
        (random.nextDouble() - 0.5) * 0.003 * velocityScale,
        (0.01 + random.nextDouble() * 0.01) * velocityScale,
        (random.nextDouble() - 0.5) * 0.003 * velocityScale);
  }
}
