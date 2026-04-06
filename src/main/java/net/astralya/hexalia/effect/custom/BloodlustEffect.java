package net.astralya.hexalia.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class BloodlustEffect extends StatusEffect {

    public BloodlustEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        List<StatusEffectInstance> bleedingEffects = entity.getStatusEffects().stream()
                .filter(instance -> {
                    Identifier id = Registries.STATUS_EFFECT.getId(instance.getEffectType().value());
                    return id != null && (id.getPath().contains("regeneration"));
                })
                .toList();

        bleedingEffects.forEach(instance -> entity.removeStatusEffect(instance.getEffectType()));

        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}