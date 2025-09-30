package net.astralya.hexalia.effect.cloud;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

import java.util.List;

public class FoulCloud extends AreaEffectCloudEntity {

    private static final float DAMAGE_PER_SECOND = 0.5F;
    private int tickCounter = 0;

    public FoulCloud(World world, double x, double y, double z, int durationSeconds) {
        super(world, x, y, z);

        setWaitTime(0);
        setRadius(3.0F);

        int totalTicks = Math.max(1, durationSeconds) * 20;
        setDuration(totalTicks);

        float radiusPerTick = -getRadius() / (float) totalTicks;
        setRadiusGrowth(radiusPerTick);

        int argb = 0xFF6A9E3B;
        setParticleType(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, argb));

        addEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 2, false, true));
        addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1, false, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient()) return;

        if (++tickCounter >= 20) {
            tickCounter = 0;
            pulseDamage();
        }
    }

    private void pulseDamage() {
        float r = getRadius();
        if (r <= 0.0F) return;

        List<LivingEntity> list = getWorld().getEntitiesByClass(
                LivingEntity.class,
                getBoundingBox(),
                LivingEntity::isAlive
        );

        for (LivingEntity target : list) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            if ((dx * dx + dz * dz) <= (r * r)) {
                LivingEntity owner = getOwner();
                if (owner != null) {
                    target.damage(getDamageSources().indirectMagic(this, owner), DAMAGE_PER_SECOND);
                } else {
                    target.damage(getDamageSources().magic(), DAMAGE_PER_SECOND);
                }
            }
        }
    }

    public void setCloudOwner(LivingEntity owner) {
        setOwner(owner);
    }
}