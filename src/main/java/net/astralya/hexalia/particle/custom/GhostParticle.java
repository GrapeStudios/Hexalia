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
    private final double orbitOffset;
    private final float baseSize;
    private final double centerX;
    private final double centerZ;
    private final double orbitRadius;
    private final double upwardDrift;

    protected GhostParticle(ClientWorld world, double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.orbitOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.centerX = x;
        this.centerZ = z;
        this.orbitRadius = 0.025 + this.random.nextDouble() * 0.03;
        this.upwardDrift = 0.003 + this.random.nextDouble() * 0.002;
        this.velocityX = 0.0;
        this.velocityY = this.upwardDrift;
        this.velocityZ = 0.0;
        this.baseSize = 0.05f + this.random.nextFloat() * 0.03f;
        this.scale = this.baseSize;
        this.maxAge = 50 + this.random.nextInt(20);
        this.gravityStrength = 0.0f;
        this.velocityMultiplier = 0.92f;
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
                ? MathHelper.clamp(lifeT / 0.2f, 0.0f, 1.0f) * 0.35f
                : lifeT > 0.75f
                  ? MathHelper.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f) * 0.35f
                  : 0.35f;

        double angle = this.orbitOffset + this.age * 0.08;
        double targetX = this.centerX + Math.cos(angle) * this.orbitRadius;
        double targetZ = this.centerZ + Math.sin(angle) * this.orbitRadius;

        this.velocityX = (targetX - this.x) * 0.08;
        this.velocityZ = (targetZ - this.z) * 0.08;
        this.velocityY = this.upwardDrift;

        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public float getSize(float tickProgress) {
        float lifeT = ((float) this.age + tickProgress) / (float) this.maxAge;
        float scaleFactor = lifeT < 0.5f
                ? 1.0f + lifeT * 0.2f
                : 1.1f - (lifeT - 0.5f) * 0.2f;
        return this.baseSize * scaleFactor;
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