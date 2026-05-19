package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class InfusedBubbleParticle extends SimpleAnimatedParticle {
  protected InfusedBubbleParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      SpriteSet sprites) {
    super(level, x, y, z, sprites, 0.01F);
    xd = velocityX;
    yd = velocityY;
    zd = velocityZ;
    quadSize *= 0.5F + random.nextInt(1);
    lifetime = 10 + random.nextInt(12);
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
      return new InfusedBubbleParticle(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);
    }
  }
}
