package net.astralya.hexalia.particle.custom;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class SporeParticle extends SpriteBillboardParticle {

    private final SpriteProvider sprites;
    private final double swayOffset;

    public SporeParticle(ClientWorld world, double x, double y, double z,
                         double velocityX, double velocityY, double velocityZ,
                         SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = spriteProvider;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.velocityX = velocityX + (this.random.nextDouble() - 0.5) * 0.02;
        this.velocityY = 0.01 + this.random.nextDouble() * 0.015;
        this.velocityZ = velocityZ + (this.random.nextDouble() - 0.5) * 0.02;
        this.scale *= 0.12f;
        this.maxAge = 80 + this.random.nextInt(40);
        this.gravityStrength = 0.0f;
        this.velocityMultiplier = 0.98f;
        this.collidesWithWorld = false;
        this.alpha = 0.0f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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

        this.setSpriteForAge(this.sprites);

        float lifeT = (float) this.age / (float) this.maxAge;
        this.alpha = lifeT < 0.15f
                ? lifeT / 0.15f
                : lifeT > 0.75f
                ? MathHelper.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f)
                : 1.0f;

        double sway = Math.sin((this.age + this.swayOffset) * 0.08) * 0.003;
        this.velocityX += sway;
        this.velocityZ += Math.cos((this.age + this.swayOffset) * 0.08) * 0.003;
        this.velocityY *= 0.97;

        this.move(this.velocityX, this.velocityY, this.velocityZ);
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
            return new SporeParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}