package net.astralya.hexalia.effect.cloud;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class FoulCloud extends AreaEffectCloud {

    private static final float DAMAGE_PER_SECOND = 0.5F;
    private int tickCounter = 0;

    public FoulCloud(Level level, double x, double y, double z, int durationSeconds) {
        super(level, x, y, z);

        this.setWaitTime(0);
        this.setRadius(3.0F);

        int totalTicks = Math.max(1, durationSeconds) * 20;
        this.setDuration(totalTicks);

        float radiusPerTick = -this.getRadius() / (float) totalTicks;
        this.setRadiusPerTick(radiusPerTick);

        this.setParticle(ParticleTypes.ENTITY_EFFECT);
        this.setFixedColor(0x6A9E3B);

        this.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2, false, true));
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            pulseDamage();
        }
    }

    private void pulseDamage() {
        float r = this.getRadius();
        if (r <= 0.0F) return;

        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
        for (LivingEntity target : list) {
            if (!target.isAlive() || !target.isAffectedByPotions()) continue;

            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                LivingEntity owner = this.getOwner();
                if (owner != null) {
                    target.hurt(this.damageSources().indirectMagic(this, owner), DAMAGE_PER_SECOND);
                } else {
                    target.hurt(this.damageSources().magic(), DAMAGE_PER_SECOND);
                }
            }
        }
    }

    public void setCloudOwner(LivingEntity owner) {
        this.setOwner(owner);
    }
}