package net.astralya.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class BloodlustEffect extends MobEffect {
    protected final double modifier;

    public BloodlustEffect(MobEffectCategory category, int color, double modifier) {
        super(category, color);
        this.modifier = modifier;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.hasEffect(MobEffects.REGENERATION)) {
            livingEntity.removeEffect(MobEffects.REGENERATION);
            return;
        }

        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return this.modifier * (double) (amplifier + 1);
    }
}