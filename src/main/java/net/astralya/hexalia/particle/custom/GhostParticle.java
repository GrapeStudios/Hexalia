package net.astralya.hexalia.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GhostParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    public GhostParticle(ClientWorld world, double x, double y, double z,
                         double velocityX, double velocityY, double velocityZ,
                         SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.velocityMultiplier = 0.92F;
        this.gravityStrength = 0.05F;

        this.velocityX *= 0.25;
        this.velocityY *= 0.25;
        this.velocityZ *= 0.25;

        this.setSpriteForAge(spriteProvider);
        this.scale = 0.2F;
        this.maxAge = 30;
        this.alpha = 0.8F;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
        this.alpha = 0.8F * (1.0F - ((float) this.age / (float) this.maxAge));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getSize(float tickDelta) {
        float ageRatio = ((float) this.age + tickDelta) / (float) this.maxAge;
        return this.scale * (0.8F - ageRatio * ageRatio * 0.3F);
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new GhostParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
