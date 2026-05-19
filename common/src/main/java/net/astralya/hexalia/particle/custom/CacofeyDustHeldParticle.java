package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CacofeyDustHeldParticle extends TextureSheetParticle {

  private final SpriteSet sprites;

  public CacofeyDustHeldParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      SpriteSet spriteSet) {
    super(level, x, y, z, velocityX, velocityY, velocityZ);
    this.sprites = spriteSet;
    this.xd = velocityX + (this.random.nextDouble() - 0.5) * 0.02;
    this.yd = 0.02 + this.random.nextDouble() * 0.015;
    this.zd = velocityZ + (this.random.nextDouble() - 0.5) * 0.02;
    this.quadSize *= 0.18f;
    this.lifetime = 20 + this.random.nextInt(10);
    this.gravity = 0.0f;
    this.friction = 0.96f;
    this.hasPhysics = false;
    this.alpha = 0.0f;
    this.rCol = 1.0f;
    this.gCol = 0.82f + this.random.nextFloat() * 0.1f;
    this.bCol = 0.3f + this.random.nextFloat() * 0.2f;
    this.pickSprite(spriteSet);
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  @Override
  public void tick() {
    this.xo = this.x;
    this.yo = this.y;
    this.zo = this.z;
    if (this.age++ >= this.lifetime) {
      this.remove();
      return;
    }
    this.pickSprite(this.sprites);
    float lifeT = (float) this.age / (float) this.lifetime;
    this.alpha = lifeT < 0.2f ? lifeT / 0.2f : Mth.clamp((1.0f - lifeT) / 0.4f, 0.0f, 1.0f);
    this.quadSize *= 0.97f;
    this.yd *= 0.95;
    this.move(this.xd, this.yd, this.zd);
  }

  public static class Factory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public Factory(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(
        SimpleParticleType type,
        ClientLevel world,
        double x,
        double y,
        double z,
        double vx,
        double vy,
        double vz) {
      return new CacofeyDustHeldParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
    }
  }
}
