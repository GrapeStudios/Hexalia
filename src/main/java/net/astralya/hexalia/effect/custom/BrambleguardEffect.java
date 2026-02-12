package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BrambleguardEffect extends MobEffect {

    public BrambleguardEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.hasEffect(ModMobEffects.BLEEDING)) {
            entity.removeEffect(ModMobEffects.BLEEDING);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
