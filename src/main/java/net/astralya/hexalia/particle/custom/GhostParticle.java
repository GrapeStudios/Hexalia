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

    protected GhostParticle(ClientLevel level, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteSet = spriteSet;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0D;
        this.xd = velocityX + (this.random.nextDouble() - 0.5D) * 0.012D;
        this.yd = velocityY;
        this.zd = velocityZ + (this.random.nextDouble() - 0.5D) * 0.012D;
        this.baseSize = 0.06F + this.random.nextFloat() * 0.04F;
        this.quadSize = this.baseSize;
        this.lifetime = 70 + this.random.nextInt(30);
        this.gravity = 0.0F;
        this.friction = 0.98F;
        this.hasPhysics = false;
        this.alpha = 0.0F;
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
        this.alpha = lifeT < 0.2F
                ? Mth.clamp(lifeT / 0.2F, 0.0F, 1.0F) * 0.55F
                : lifeT > 0.7F
                ? Mth.clamp((1.0F - lifeT) / 0.3F, 0.0F, 1.0F) * 0.55F
                : 0.55F;

        double sway = Math.sin((this.age + this.swayOffset) * 0.06D) * 0.004D;
        this.xd += sway;
        this.zd += Math.cos((this.age + this.swayOffset) * 0.06D) * 0.004D;
        this.yd *= 0.985D;

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public float getQuadSize(float partialTick) {
        float lifeT = ((float) this.age + partialTick) / (float) this.lifetime;
        float scale = lifeT < 0.5F
                ? 1.0F + lifeT * 0.6F
                : 1.3F - (lifeT - 0.5F) * 0.6F;
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new GhostParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
        }
    }
}