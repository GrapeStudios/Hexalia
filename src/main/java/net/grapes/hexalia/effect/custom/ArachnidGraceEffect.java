package net.grapes.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class ArachnidGraceEffect extends MobEffect {
    private static final double CLIMB_SPEED = 0.2;

    public ArachnidGraceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.horizontalCollision && !entity.isShiftKeyDown()) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y <= CLIMB_SPEED) {
                entity.setDeltaMovement(motion.x, CLIMB_SPEED, motion.z);
                entity.fallDistance = 0.0F;
            }
        }

        if (entity.hasEffect(MobEffects.POISON)) {
            entity.removeEffect(MobEffects.POISON);
        }

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}