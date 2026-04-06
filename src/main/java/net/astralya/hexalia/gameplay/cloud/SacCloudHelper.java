package net.astralya.hexalia.gameplay.cloud;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

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
        cloud.setRadiusGrowth(-initialRadius / (float) totalTicks);

        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        cloud.setParticleType(new DustParticleEffect(new Vector3f(r, g, b), 1.0F));
    }

    public static HoldShrinkPlan configureWithHold(AreaEffectCloudEntity cloud, int durationSeconds, int holdSeconds, float initialRadius, int rgb) {
        cloud.setWaitTime(0);
        cloud.setRadius(initialRadius);
        int totalTicks = Math.max(1, durationSeconds) * 20;
        cloud.setDuration(totalTicks);
        int holdTicks = Math.max(0, holdSeconds) * 20;
        if (holdTicks >= totalTicks) {
            holdTicks = Math.max(0, totalTicks - 1);
        }
        cloud.setRadiusGrowth(0.0F);
        int shrinkTicks = Math.max(1, totalTicks - holdTicks);
        float shrinkPerTick = -initialRadius / (float) shrinkTicks;

        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        cloud.setParticleType(new DustParticleEffect(new Vector3f(r, g, b), 1.0F));

        return new HoldShrinkPlan(holdTicks, shrinkPerTick);
    }

    public static void startShrinkIfReady(AreaEffectCloudEntity cloud, int ageTicks, HoldShrinkPlan plan, boolean[] startedFlag) {
        if (startedFlag[0] || ageTicks < plan.holdTicks()) {
            return;
        }
        cloud.setRadiusGrowth(plan.shrinkPerTick());
        startedFlag[0] = true;
    }

    public static void forEachLivingInRadius(AreaEffectCloudEntity cloud, Consumer<LivingEntity> action) {
        float radius = cloud.getRadius();
        if (radius <= 0.0F) {
            return;
        }
        List<LivingEntity> list = cloud.getWorld().getNonSpectatingEntities(LivingEntity.class, cloud.getBoundingBox());
        double centerX = cloud.getX();
        double centerZ = cloud.getZ();
        double radiusSq = radius * radius;
        for (LivingEntity target : list) {
            if (!target.isAlive() || !target.isAffectedBySplashPotions()) {
                continue;
            }
            double dx = target.getX() - centerX;
            double dz = target.getZ() - centerZ;
            if (dx * dx + dz * dz > radiusSq) {
                continue;
            }
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