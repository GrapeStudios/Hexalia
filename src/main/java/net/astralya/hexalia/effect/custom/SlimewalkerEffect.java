package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SlimewalkerEffect extends StatusEffect {

    public SlimewalkerEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.isOnGround()) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 0, false, false, true));
        }
        if (livingEntity.isOnGround() && livingEntity.isSneaking()) {
            Vec3d movement = livingEntity.getVelocity();
            livingEntity.setVelocity(movement.x, 1.0D, movement.z);
            livingEntity.velocityModified = true;
            livingEntity.playSound(SoundEvents.ENTITY_SLIME_JUMP, 1.0F, 1.0F);
            for (int i = 0; i < 8; ++i) {
                float a = livingEntity.getWorld().random.nextFloat() * ((float) Math.PI * 2F);
                float a1 = livingEntity.getWorld().random.nextFloat() * 0.5F + 0.5F;
                float a2 = MathHelper.sin(a) * 0.5F * a1;
                float a3 = MathHelper.cos(a) * 0.5F * a1;
                livingEntity.getWorld().addParticle(
                        ParticleTypes.ITEM_SLIME,
                        livingEntity.getX() + a2,
                        livingEntity.getY(),
                        livingEntity.getZ() + a3,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
            return true;
        }
        livingEntity.fallDistance = 0.0F;
        return super.applyUpdateEffect(livingEntity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}