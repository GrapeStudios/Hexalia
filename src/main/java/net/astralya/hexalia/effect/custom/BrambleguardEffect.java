package net.astralya.hexalia.effect.custom;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BrambleguardEffect extends StatusEffect {

    public BrambleguardEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(ModMobEffects.BLEEDING)) {
            entity.removeStatusEffect(ModMobEffects.BLEEDING);
        }

        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}