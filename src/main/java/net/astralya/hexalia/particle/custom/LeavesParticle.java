package net.astralya.hexalia.particle.custom;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class LeavesParticle extends AnimatedParticle {

    public LeavesParticle(ClientWorld world,
                          double x, double y, double z,
                          double velocityX, double velocityY, double velocityZ,
                          SpriteProvider sprites) {
        super(world, x, y, z, sprites, 0.01F);

        this.collidesWithWorld = false;

        this.velocityMultiplier = 0.96F;

        this.gravityStrength = 0.0F;

        if (velocityX == 0 && velocityY == 0 && velocityZ == 0) {
            this.velocityX = (this.random.nextDouble() - 0.5D) * 0.02D;
            this.velocityY =  this.random.nextDouble()            * 0.02D;
            this.velocityZ = (this.random.nextDouble() - 0.5D) * 0.02D;
        } else {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
        }

        this.scale *= (0.2F + this.random.nextFloat() * 0.4F);

        this.maxAge = 14 + this.random.nextInt(6);

        int c = 15916745;
        this.setColor(
                ((c >> 16) & 255) / 255f,
                ((c >>  8) & 255) / 255f,
                ( c        & 255) / 255f
        );

        this.setSpriteForAge(sprites);
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type,
                                       ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new LeavesParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.sprites);
        }
    }
}
