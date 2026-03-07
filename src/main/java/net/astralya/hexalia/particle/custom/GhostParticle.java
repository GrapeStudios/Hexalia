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
    private final double swayOffset;
    private final float baseSize;

    protected GhostParticle(ClientLevel world, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = spriteSet;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.xd = velocityX + (this.random.nextDouble() - 0.5) * 0.012;
        this.yd = velocityY;
        this.zd = velocityZ + (this.random.nextDouble() - 0.5) * 0.012;
        this.baseSize = 0.06f + this.random.nextFloat() * 0.04f;
        this.quadSize = this.baseSize;
        this.lifetime = 70 + this.random.nextInt(30);
        this.gravity = 0.0f;
        this.friction = 0.98f;
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
                ? Mth.clamp(lifeT / 0.2f, 0.0f, 1.0f) * 0.55f
                : lifeT > 0.7f
                ? Mth.clamp((1.0f - lifeT) / 0.3f, 0.0f, 1.0f) * 0.55f
                : 0.55f;

        double sway = Math.sin((this.age + this.swayOffset) * 0.06) * 0.004;
        this.xd += sway;
        this.zd += Math.cos((this.age + this.swayOffset) * 0.06) * 0.004;
        this.yd *= 0.985;

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float lifeT = ((float) this.age + scaleFactor) / (float) this.lifetime;
        float scale = lifeT < 0.5f
                ? 1.0f + lifeT * 0.6f
                : 1.3f - (lifeT - 0.5f) * 0.6f;
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