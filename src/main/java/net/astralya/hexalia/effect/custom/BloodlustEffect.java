package net.astralya.hexalia.effect.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

public class BloodlustEffect extends MobEffect {

    public BloodlustEffect(MobEffectCategory category, int color, double modifier) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        List<MobEffectInstance> bleedingEffects = entity.getActiveEffects().stream()
                .filter(instance -> {
                    ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());
                    return id != null && (id.getPath().contains("regeneration"));
                })
                .toList();

        bleedingEffects.forEach(instance -> entity.removeEffect(instance.getEffect()));

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
