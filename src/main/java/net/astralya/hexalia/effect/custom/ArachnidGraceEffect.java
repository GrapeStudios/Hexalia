package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class ArachnidGraceEffect extends StatusEffect {
    private static final double CLIMB_SPEED = 0.2;

    public ArachnidGraceEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.horizontalCollision && !entity.isSneaking()) {
            Vec3d motion = entity.getVelocity();
            if (motion.y <= CLIMB_SPEED) {
                entity.setVelocity(motion.x, CLIMB_SPEED, motion.z);
                entity.fallDistance = 0.0F;
            }
        }

        if (entity.hasStatusEffect(StatusEffects.POISON)) {
            entity.removeStatusEffect(StatusEffects.POISON);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}