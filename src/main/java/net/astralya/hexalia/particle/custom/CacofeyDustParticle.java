package net.astralya.hexalia.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CacofeyDustParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final double swayOffset;

    public CacofeyDustParticle(ClientLevel level, double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ,
                               SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites   = spriteSet;
        this.swayOffset = this.random.nextDouble() * Math.PI * 2.0;
        this.xd        = velocityX + (this.random.nextDouble() - 0.5) * 0.008;
        this.yd        = 0.005 + this.random.nextDouble() * 0.007;
        this.zd        = velocityZ + (this.random.nextDouble() - 0.5) * 0.008;
        this.quadSize  *= 0.07f;
        this.lifetime  = 50 + this.random.nextInt(25);
        this.gravity   = 0.0f;
        this.friction  = 0.99f;
        this.hasPhysics = false;
        this.alpha     = 0.0f;
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
        float lifeT  = (float) this.age / (float) this.lifetime;
        this.alpha   = lifeT < 0.15f
                ? lifeT / 0.15f
                : lifeT > 0.75f
                ? Mth.clamp((1.0f - lifeT) / 0.25f, 0.0f, 1.0f)
                : 1.0f;
        double sway  = Math.sin((this.age + this.swayOffset) * 0.09) * 0.001;
        this.xd      += sway;
        this.zd      += Math.cos((this.age + this.swayOffset) * 0.09) * 0.001;
        this.yd      *= 0.98;
        this.move(this.xd, this.yd, this.zd);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new CacofeyDustParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}