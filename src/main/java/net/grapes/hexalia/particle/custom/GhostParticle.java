package net.grapes.hexalia.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GhostParticle extends AscendingParticle {
    protected GhostParticle(ClientWorld world, double x, double y, double z, double velocityX,
                            double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1F, 0.1F, 0.1F,
                velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 1.0F, 8,
                -0.1F, true);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e,
                                       double f, double g, double h, double i) {
            return new GhostParticle(clientWorld, d, e, f, g, h, i, 0.5F, this.spriteProvider);
        }
    }
}
