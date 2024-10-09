package net.grapes.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class BloodlustEffect extends MobEffect {
    protected double modifier;

    public BloodlustEffect(MobEffectCategory pCategory, int pColor, double modifier) {
        super(pCategory, pColor);
        this.modifier = modifier;
    }

    public double adjustModifierAmount(int amplifier, AttributeModifier modifier) {
        return this.modifier * (double)(amplifier + 1);
    }
}
