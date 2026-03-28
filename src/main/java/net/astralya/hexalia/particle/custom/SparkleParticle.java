package net.astralya.hexalia.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SparkleParticle extends AnimatedParticle {

    private final float baseSize;
    private final float shimmerSpeed;
    private final float shimmerStrength;
    private final float yBoost;
    private final double startX;
    private final double startZ;

    protected SparkleParticle(
            ClientWorld world,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ,
            SpriteProvider sprites
    ) {
        super(world, x, y, z, sprites, 0.01F);
        this.collidesWithWorld = false;
        this.velocityMultiplier = 0.96F;
        this.gravityStrength = 0.0F;
        this.startX = x;
        this.startZ = z;
        if (velocityX == 0.0D && velocityY == 0.0D && velocityZ == 0.0D) {
            this.velocityX = (random.nextDouble() - 0.5D) * 0.004D;
            this.velocityY = 0.008D + random.nextDouble() * 0.010D;
            this.velocityZ = (random.nextDouble() - 0.5D) * 0.004D;
        } else {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
        }
        this.baseSize = 0.08F + random.nextFloat() * 0.08F;
        this.scale *= this.baseSize;
        this.maxAge = 18 + random.nextInt(14);
        this.shimmerSpeed = 0.18F + random.nextFloat() * 0.25F;
        this.shimmerStrength = 0.010F + random.nextFloat() * 0.010F;
        this.yBoost = 0.002F + random.nextFloat() * 0.004F;
        this.setColor(15916745);
        this.alpha = 0.0F;
        this.setSpriteForAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.dead) return;
        float t = (float) this.age / (float) this.maxAge;
        float fadeIn = MathHelper.clamp(t / 0.15F, 0.0F, 1.0F);
        float fadeOut = MathHelper.clamp((1.0F - t) / 0.35F, 0.0F, 1.0F);
        this.alpha = Math.min(fadeIn, fadeOut);
        float pulse = 0.85F + 0.25F * MathHelper.sin((this.age + this.random.nextFloat()) * 0.35F);
        this.scale = this.baseSize * pulse;
        double wobble = MathHelper.sin((this.age + this.random.nextFloat()) * this.shimmerSpeed) * this.shimmerStrength;
        this.x = this.startX + wobble + (this.x - this.startX) * 0.98D;
        this.z = this.startZ + wobble + (this.z - this.startZ) * 0.98D;
        this.velocityY += this.yBoost;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientWorld world,
                double x, double y, double z,
                double velocityX, double velocityY, double velocityZ
        ) {
            return new SparkleParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}