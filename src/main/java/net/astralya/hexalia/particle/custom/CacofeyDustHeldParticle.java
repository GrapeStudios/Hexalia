package net.astralya.hexalia.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CacofeyDustHeldParticle extends SpriteBillboardParticle {

    private final SpriteProvider sprites;

    public CacofeyDustHeldParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = spriteProvider;
        this.velocityX = velocityX + (this.random.nextDouble() - 0.5) * 0.02;
        this.velocityY = 0.02 + this.random.nextDouble() * 0.015;
        this.velocityZ = velocityZ + (this.random.nextDouble() - 0.5) * 0.02;
        this.scale *= 0.18f;
        this.maxAge = 20 + this.random.nextInt(10);
        this.gravityStrength = 0.0f;
        this.velocityMultiplier = 0.96f;
        this.collidesWithWorld = false;
        this.alpha = 0.0f;
        this.red = 1.0f;
        this.green = 0.82f + this.random.nextFloat() * 0.1f;
        this.blue = 0.3f + this.random.nextFloat() * 0.2f;
        this.setSprite(spriteProvider);
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

        this.setSprite(this.sprites);

        float lifeT = (float) this.age / (float) this.maxAge;
        this.alpha = lifeT < 0.2f
                ? lifeT / 0.2f
                : MathHelper.clamp((1.0f - lifeT) / 0.4f, 0.0f, 1.0f);

        this.scale *= 0.97f;
        this.velocityY *= 0.95;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new CacofeyDustHeldParticle(world, x, y, z, vx, vy, vz, this.spriteProvider);
        }
    }
}