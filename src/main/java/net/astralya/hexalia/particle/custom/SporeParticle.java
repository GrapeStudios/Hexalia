package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SporeParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public SporeParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = spriteSet;

        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;

        this.quadSize *= 0.1f;
        this.lifetime = 40 + this.random.nextInt(12);

        this.gravity = 0.01f;
        this.friction = 0.92f;

        this.hasPhysics = true;

        this.alpha = 0.0f;
        this.pickSprite(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.pickSprite(this.sprites);

        float lifeT = (float) this.age / (float) this.lifetime;
        this.alpha = Mth.clamp(lifeT * 6.0f, 0.0f, 1.0f);
        if (lifeT > 0.75f) {
            this.alpha *= Mth.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f);
        }

        this.xd += (this.random.nextDouble() - 0.5) * 0.0025;
        this.zd += (this.random.nextDouble() - 0.5) * 0.0025;

        this.yd -= this.gravity;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.xd *= 0.65;
            this.zd *= 0.65;
            this.yd *= -0.15;
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz) {
            return new SporeParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}