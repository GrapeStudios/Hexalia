package net.astralya.hexalia.effect.cloud;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class FrostCloud extends AreaEffectCloud {

    private static final int FREEZE_INCREMENT_PER_TICK = 5;

    public FrostCloud(Level level, double x, double y, double z, int durationSeconds) {
        super(level, x, y, z);
        this.setWaitTime(0);
        this.setRadius(3.0F);
        int totalTicks = Math.max(1, durationSeconds) * 20;
        this.setDuration(totalTicks);
        float radiusPerTick = -this.getRadius() / (float) totalTicks;
        this.setRadiusPerTick(radiusPerTick);
        int rgb = 0x9FD9FF;
        int argb = FastColor.ARGB32.opaque(rgb);
        this.setParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, argb));
        this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        float r = this.getRadius();
        if (r <= 0.0F) return;
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
        for (LivingEntity target : list) {
            if (!target.isAlive() || !target.isAffectedByPotions()) continue;
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                int required = target.getTicksRequiredToFreeze();
                int current = target.getTicksFrozen();
                if (current < required && target.canFreeze()) {
                    target.setTicksFrozen(Math.min(required, current + FREEZE_INCREMENT_PER_TICK));
                }
            }
        }
    }

    public void setCloudOwner(LivingEntity owner) {
        this.setOwner(owner);
    }
}