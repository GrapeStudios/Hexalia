package net.astralya.hexalia.particle.custom;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

public class GhostParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    private final double swayOffset;
    private final float baseSize;

    protected GhostParticle(ClientWorld world, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.velocityX = velocityX + (this.random.nextDouble() - 0.5) * 0.012;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ + (this.random.nextDouble() - 0.5) * 0.012;
        this.baseSize = 0.06f + this.random.nextFloat() * 0.04f;
        this.scale = this.baseSize;
        this.maxAge = 70 + this.random.nextInt(30);
        this.gravityStrength = 0.0f;
        this.velocityMultiplier = 0.98f;
        this.collidesWithWorld = false;
        this.alpha = 0.0f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        this.setSpriteForAge(this.spriteProvider);

        float lifeT = (float) this.age / (float) this.maxAge;

        this.alpha = lifeT < 0.2f
                ? MathHelper.clamp(lifeT / 0.2f, 0.0f, 1.0f) * 0.55f
                : lifeT > 0.7f
                ? MathHelper.clamp((1.0f - lifeT) / 0.3f, 0.0f, 1.0f) * 0.55f
                : 0.55f;

        double sway = Math.sin((this.age + this.swayOffset) * 0.06) * 0.004;
        this.velocityX += sway;
        this.velocityZ += Math.cos((this.age + this.swayOffset) * 0.06) * 0.004;
        this.velocityY *= 0.985;

        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public float getSize(float tickProgress) {
        float lifeT = ((float) this.age + tickProgress) / (float) this.maxAge;
        float scale = lifeT < 0.5f
                ? 1.0f + lifeT * 0.6f
                : 1.3f - (lifeT - 0.5f) * 0.6f;
        return this.baseSize * scale;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new GhostParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}