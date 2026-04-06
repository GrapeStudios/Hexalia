package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SpikeskinEffect extends StatusEffect {

    public SpikeskinEffect(StatusEffectCategory category, int color, double modifier) {
        super(category, color);
    }

    @Override
    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        return modifier.getValue() * (double) (amplifier + 1);
    }
}