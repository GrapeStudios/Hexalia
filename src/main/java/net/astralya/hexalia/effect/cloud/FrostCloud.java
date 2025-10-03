package net.astralya.hexalia.effect.cloud;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

import java.util.List;

public class FrostCloud extends AreaEffectCloudEntity {

    private static final int FREEZE_INCREMENT_PER_TICK = 5;

    public FrostCloud(World world, double x, double y, double z, int durationSeconds) {
        super(world, x, y, z);

        setWaitTime(0);
        setRadius(3.0F);

        int totalTicks = Math.max(1, durationSeconds) * 20;
        setDuration(totalTicks);

        float radiusPerTick = -getRadius() / (float) totalTicks;
        setRadiusGrowth(radiusPerTick);

        setParticleType(ParticleTypes.ENTITY_EFFECT);
        setColor(0x9FD9FF);

        addEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient()) return;

        float r = getRadius();
        if (r <= 0.0F) return;

        List<LivingEntity> targets = getWorld().getEntitiesByClass(
                LivingEntity.class,
                getBoundingBox(),
                LivingEntity::isAlive
        );

        for (LivingEntity target : targets) {
            if (!target.canFreeze()) continue;

            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                int required = target.getMinFreezeDamageTicks();
                int current = target.getFrozenTicks();
                if (current < required) {
                    target.setFrozenTicks(Math.min(required, current + FREEZE_INCREMENT_PER_TICK));
                }
            }
        }
    }

    public void setCloudOwner(LivingEntity owner) {
        setOwner(owner);
    }
}