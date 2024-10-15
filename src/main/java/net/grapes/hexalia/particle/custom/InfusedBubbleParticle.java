package net.grapes.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class InfusedBubbleParticle extends TextureSheetParticle {
    public InfusedBubbleParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteSet spriteSet) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;

        this.quadSize *= scaleMultiplier;
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.yd -= 0.1F;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) this.age + pScaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0f - f * f * 0.3f);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new InfusedBubbleParticle(world, x, y, z, vx, vy, vz, 0.5f,this.spriteSet);
        }
    }
}
