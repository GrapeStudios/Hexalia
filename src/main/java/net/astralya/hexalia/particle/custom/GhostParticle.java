package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class GhostParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected GhostParticle(ClientLevel world, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = spriteSet;

        this.friction = 0.92F;
        this.gravity = 0.05F;
        this.xd *= 0.25;
        this.yd *= 0.25;
        this.zd *= 0.25;

        this.setSpriteFromAge(spriteSet);
        this.quadSize = 0.2F;
        this.lifetime = 30;
        this.alpha = 0.8F;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        this.alpha = 0.8F * (1 - ((float) this.age / this.lifetime));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float ageRatio = ((float) this.age + scaleFactor) / this.lifetime;
        return this.quadSize * (0.8F - ageRatio * ageRatio * 0.3F);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new GhostParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
        }
    }
}
