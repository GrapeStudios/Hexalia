package net.astralya.hexalia.particle.custom;

import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class InfusedBubbleParticle extends AnimatedParticle {

    public InfusedBubbleParticle(ClientWorld world, double x, double y, double z,
                                    double velocityX, double velocityY, double velocityZ,
                                    SpriteProvider sprites) {
        super(world, x, y, z, sprites, 0.01F);

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.scale *= 0.5f + this.random.nextFloat();
        this.maxAge = 10 + this.random.nextInt(12);

        this.setColor(242 / 255f, 222 / 255f, 201 / 255f);

        this.setSpriteForAge(sprites);
    }


    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new InfusedBubbleParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.sprites);
        }
    }
}

