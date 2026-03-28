package net.astralya.hexalia.effect.cloud;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.function.Consumer;

public final class SacCloudHelper {

    private SacCloudHelper() {
    }

    public record HoldShrinkPlan(int holdTicks, float shrinkPerTick) {
    }

    public static void configure(AreaEffectCloudEntity cloud, int durationSeconds, float initialRadius, int rgb) {
        cloud.setWaitTime(0);
        cloud.setRadius(initialRadius);
        int totalTicks = Math.max(1, durationSeconds) * 20;
        cloud.setDuration(totalTicks);
        float radiusPerTick = -initialRadius / (float) totalTicks;
        cloud.setRadiusGrowth(radiusPerTick);
        int argb = ColorHelper.Argb.fullAlpha(rgb);
        cloud.setParticleType(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, argb));
    }

    public static HoldShrinkPlan configureWithHold(AreaEffectCloudEntity cloud, int durationSeconds, int holdSeconds, float initialRadius, int rgb) {
        cloud.setWaitTime(0);
        cloud.setRadius(initialRadius);
        int totalTicks = Math.max(1, durationSeconds) * 20;
        cloud.setDuration(totalTicks);
        int holdTicks = Math.max(0, holdSeconds) * 20;
        if (holdTicks >= totalTicks) holdTicks = Math.max(0, totalTicks - 1);
        cloud.setRadiusGrowth(0.0F);
        int shrinkTicks = Math.max(1, totalTicks - holdTicks);
        float shrinkPerTick = -initialRadius / (float) shrinkTicks;
        int argb = ColorHelper.Argb.fullAlpha(rgb);
        cloud.setParticleType(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, argb));
        return new HoldShrinkPlan(holdTicks, shrinkPerTick);
    }

    public static void startShrinkIfReady(AreaEffectCloudEntity cloud, int ageTicks, HoldShrinkPlan plan, boolean[] startedFlag) {
        if (startedFlag[0]) return;
        if (ageTicks < plan.holdTicks()) return;
        cloud.setRadiusGrowth(plan.shrinkPerTick());
        startedFlag[0] = true;
    }

    public static void forEachLivingInRadius(AreaEffectCloudEntity cloud, Consumer<LivingEntity> action) {
        float r = cloud.getRadius();
        if (r <= 0.0F) return;
        List<LivingEntity> list = cloud.getWorld().getNonSpectatingEntities(LivingEntity.class, cloud.getBoundingBox());
        double cx = cloud.getX();
        double cz = cloud.getZ();
        double rr = (double) r * (double) r;
        for (LivingEntity target : list) {
            if (!target.isAlive() || !target.isAffectedBySplashPotions()) continue;
            double dx = target.getX() - cx;
            double dz = target.getZ() - cz;
            if ((dx * dx + dz * dz) > rr) continue;
            action.accept(target);
        }
    }

    public static void damageMagic(AreaEffectCloudEntity cloud, LivingEntity target, float amount) {
        LivingEntity owner = cloud.getOwner();
        if (owner != null) {
            target.damage(cloud.getWorld().getDamageSources().indirectMagic(cloud, owner), amount);
        } else {
            target.damage(cloud.getWorld().getDamageSources().magic(), amount);
        }
    }
}