package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SparkleParticle extends SimpleAnimatedParticle {
  private final float baseSize;
  private final float shimmerSpeed;
  private final float shimmerStrength;
  private final float yBoost;
  private final double startX;
  private final double startZ;

  protected SparkleParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      SpriteSet sprites) {
    super(level, x, y, z, sprites, 0.01F);
    hasPhysics = false;
    friction = 0.96F;
    gravity = 0.0F;
    startX = x;
    startZ = z;

    if (velocityX == 0.0D && velocityY == 0.0D && velocityZ == 0.0D) {
      xd = (random.nextDouble() - 0.5D) * 0.004D;
      yd = 0.008D + random.nextDouble() * 0.010D;
      zd = (random.nextDouble() - 0.5D) * 0.004D;
    } else {
      xd = velocityX;
      yd = velocityY;
      zd = velocityZ;
    }

    baseSize = 0.08F + random.nextFloat() * 0.08F;
    quadSize *= baseSize;
    lifetime = 18 + random.nextInt(14);
    shimmerSpeed = 0.18F + random.nextFloat() * 0.25F;
    shimmerStrength = 0.010F + random.nextFloat() * 0.010F;
    yBoost = 0.002F + random.nextFloat() * 0.004F;
    setColor(15916745);
    alpha = 0.0F;
    setSpriteFromAge(sprites);
  }

  @Override
  public void tick() {
    super.tick();
    if (removed) {
      return;
    }

    float ageProgress = (float) age / (float) lifetime;
    float fadeIn = Mth.clamp(ageProgress / 0.15F, 0.0F, 1.0F);
    float fadeOut = Mth.clamp((1.0F - ageProgress) / 0.35F, 0.0F, 1.0F);
    alpha = Math.min(fadeIn, fadeOut);

    float pulse = 0.85F + 0.25F * Mth.sin((age + random.nextFloat()) * 0.35F);
    quadSize = baseSize * pulse;

    double wobble = Mth.sin((age + random.nextFloat()) * shimmerSpeed) * shimmerStrength;
    x = startX + wobble + (x - startX) * 0.98D;
    z = startZ + wobble + (z - startZ) * 0.98D;
    yd += yBoost;
  }

  @Override
  public void move(double x, double y, double z) {
    setBoundingBox(getBoundingBox().move(x, y, z));
    setLocationFromBoundingbox();
  }

  public static class Factory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public Factory(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(
        SimpleParticleType type,
        ClientLevel level,
        double x,
        double y,
        double z,
        double velocityX,
        double velocityY,
        double velocityZ) {
      return new SparkleParticle(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);
    }
  }
}
