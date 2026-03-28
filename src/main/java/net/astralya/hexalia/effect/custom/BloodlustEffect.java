package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;

public class BloodlustEffect extends StatusEffect {

    protected final double modifier;

    public BloodlustEffect(StatusEffectCategory category, int color, double modifier) {
        super(category, color);
        this.modifier = modifier;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(StatusEffects.REGENERATION)) {
            entity.removeStatusEffect(StatusEffects.REGENERATION);
            return true;
        }

        return super.applyUpdateEffect(entity, amplifier);
    }

    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        return this.modifier * (double)(amplifier + 1);
    }
}