package net.grapes.hexalia.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SporeParticle extends SpriteBillboardParticle {
    public SporeParticle(ClientWorld world, double xCoord, double yCoord, double zCoord,
                              SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(world, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0.3f;
        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;

        this.scale *= 0.15f;
        this.maxAge = 10;
        this.setSpriteForAge(spriteSet);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteProvider) {
            this.sprites = spriteProvider;
        }
        public Particle createParticle(DefaultParticleType particleType, ClientWorld clientWorld, double x, double y,
                                       double z, double xd, double yd, double zd){
            return new SporeParticle(clientWorld, x, y, z, this.sprites, xd, yd, zd);
        }
    }
}
