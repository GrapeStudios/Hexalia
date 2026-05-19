package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LeavesParticle extends SimpleAnimatedParticle {
  protected LeavesParticle(
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

    if (velocityX == 0.0D && velocityY == 0.0D && velocityZ == 0.0D) {
      xd = (random.nextDouble() - 0.5D) * 0.02D;
      yd = random.nextDouble() * 0.02D;
      zd = (random.nextDouble() - 0.5D) * 0.02D;
    } else {
      xd = velocityX;
      yd = velocityY;
      zd = velocityZ;
    }

    quadSize *= 0.2F + random.nextFloat() * 0.4F;
    lifetime = 14 + random.nextInt(6);
    setColor(15916745);
    setSpriteFromAge(sprites);
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
      return new LeavesParticle(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);
    }
  }
}
