package net.grapes.hexalia.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class GhostParticle extends TextureSheetParticle {
    protected GhostParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteSet) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.friction = 0.98F;
        this.gravity = 0.2F;
        this.setSpriteFromAge(spriteSet);
        this.quadSize *= 0.5F;
        this.lifetime = 10;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float ageScale = ((float) this.age + scaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - ageScale * ageScale * 0.5F);  // Decrease size over time
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new GhostParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
        }
    }
}
