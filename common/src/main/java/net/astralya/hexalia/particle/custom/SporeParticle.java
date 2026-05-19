package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class SporeParticle extends TextureSheetParticle {
  private final SpriteSet sprites;
  private final double baseX;
  private final double baseY;
  private final double baseZ;
  private final double swayOffset;
  private final double verticalOffset;

  protected SporeParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      SpriteSet sprites,
      float red,
      float green,
      float blue) {
    super(level, x, y, z, velocityX, velocityY, velocityZ);

    this.sprites = sprites;

    baseX = x;
    baseY = y;
    baseZ = z;

    swayOffset = random.nextDouble() * Math.PI * 2.0D;
    verticalOffset = random.nextDouble() * Math.PI * 2.0D;

    quadSize *= 0.12F + random.nextFloat() * 0.05F;

    lifetime = 80 + random.nextInt(40);

    gravity = 0.0F;
    friction = 1.0F;
    hasPhysics = false;

    alpha = 0.0F;

    setColor(red, green, blue);

    pickSprite(sprites);
  }

  @Override
  public ParticleRenderType getRenderType() {
    return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
  }

  @Override
  public void tick() {
    xo = x;
    yo = y;
    zo = z;

    if (age++ >= lifetime) {
      remove();
      return;
    }

    pickSprite(sprites);

    float ageProgress = (float) age / (float) lifetime;

    alpha =
        ageProgress < 0.15F
            ? ageProgress / 0.15F
            : ageProgress > 0.75F ? Mth.clamp((1.0F - ageProgress) / 0.25F, 0.0F, 1.0F) : 1.0F;

    double swayX = Math.sin((age * 0.06D) + swayOffset) * 0.03D;
    double swayZ = Math.cos((age * 0.06D) + swayOffset) * 0.03D;

    double bobY = Math.sin((age * 0.04D) + verticalOffset) * 0.01D;

    x = baseX + swayX;
    y = baseY + bobY;
    z = baseZ + swayZ;
  }

  public static class Factory implements ParticleProvider<ColoredSporeParticleOptions> {
    private final SpriteSet spriteSet;

    public Factory(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(
        ColoredSporeParticleOptions options,
        ClientLevel level,
        double x,
        double y,
        double z,
        double velocityX,
        double velocityY,
        double velocityZ) {
      return new SporeParticle(
          level,
          x,
          y,
          z,
          velocityX,
          velocityY,
          velocityZ,
          spriteSet,
          options.color().x(),
          options.color().y(),
          options.color().z());
    }
  }
}
