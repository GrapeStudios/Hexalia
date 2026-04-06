package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class GhostParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;
    private final double orbitOffset;
    private final float baseSize;
    private final double centerX;
    private final double centerZ;
    private final double orbitRadius;
    private final double upwardDrift;

    protected GhostParticle(ClientLevel world, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = spriteSet;
        this.orbitOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.centerX = x;
        this.centerZ = z;
        this.orbitRadius = 0.025 + this.random.nextDouble() * 0.03;
        this.upwardDrift = 0.003 + this.random.nextDouble() * 0.002;
        this.xd = 0.0;
        this.yd = this.upwardDrift;
        this.zd = 0.0;
        this.baseSize = 0.05f + this.random.nextFloat() * 0.03f;
        this.quadSize = this.baseSize;
        this.lifetime = 50 + this.random.nextInt(20);
        this.gravity = 0.0f;
        this.friction = 0.92f;
        this.hasPhysics = false;
        this.alpha = 0.0f;
        this.setSpriteFromAge(spriteSet);
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

        this.setSpriteFromAge(this.spriteSet);

        float lifeT = (float) this.age / (float) this.lifetime;

        this.alpha = lifeT < 0.2f
                ? Mth.clamp(lifeT / 0.2f, 0.0f, 1.0f) * 0.35f
                : lifeT > 0.75f
                  ? Mth.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f) * 0.35f
                  : 0.35f;

        double angle = this.orbitOffset + this.age * 0.08;
        double targetX = this.centerX + Math.cos(angle) * this.orbitRadius;
        double targetZ = this.centerZ + Math.sin(angle) * this.orbitRadius;

        this.xd = (targetX - this.x) * 0.08;
        this.zd = (targetZ - this.z) * 0.08;
        this.yd = this.upwardDrift;

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float lifeT = ((float) this.age + scaleFactor) / (float) this.lifetime;
        float scale = lifeT < 0.5f
                ? 1.0f + lifeT * 0.2f
                : 1.1f - (lifeT - 0.5f) * 0.2f;
        return this.baseSize * scale;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
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