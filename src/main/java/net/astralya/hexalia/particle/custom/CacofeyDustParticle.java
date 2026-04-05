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
public class CacofeyDustParticle extends SpriteBillboardParticle {

    private final SpriteProvider sprites;
    private final double swayOffset;

    public CacofeyDustParticle(ClientWorld world, double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ,
                               SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = spriteProvider;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.velocityX = velocityX + (this.random.nextDouble() - 0.5) * 0.008;
        this.velocityY = 0.005 + this.random.nextDouble() * 0.007;
        this.velocityZ = velocityZ + (this.random.nextDouble() - 0.5) * 0.008;
        this.scale *= 0.07f;
        this.maxAge = 50 + this.random.nextInt(25);
        this.gravityStrength = 0.0f;
        this.velocityMultiplier = 0.99f;
        this.collidesWithWorld = false;
        this.alpha = 0.0f;
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
        this.alpha = lifeT < 0.15f
                ? lifeT / 0.15f
                : lifeT > 0.75f
                ? MathHelper.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f)
                : 1.0f;

        double sway = Math.sin((this.age + this.swayOffset) * 0.09) * 0.001;
        this.velocityX += sway;
        this.velocityZ += Math.cos((this.age + this.swayOffset) * 0.09) * 0.001;
        this.velocityY *= 0.98;

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
            return new CacofeyDustParticle(world, x, y, z, vx, vy, vz, this.spriteProvider);
        }
    }
}