package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.Configuration;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingEffect extends MobEffect {
    public BleedingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide && livingEntity.getHealth() > 0f) {
            float damage = Configuration.BLEEDING_DAMAGE.get().floatValue() + amplifier * 0.2f;
            livingEntity.hurt(livingEntity.damageSources().generic(), damage);
            return true;
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
       return true;
    }
}
