package net.grapes.hexalia.item;

import net.grapes.hexalia.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;

public class ModFoodComponents {
    public static final FoodComponent SIREN_KELP = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200), 1f).build();

    public static final FoodComponent CHILLBERRIES = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200), 1f).build();
    public static final FoodComponent CHILLBERRY_CUPCAKE = new FoodComponent.Builder().hunger(6).saturationModifier(0.8f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 1), 1f).build();

    public static final FoodComponent MANDRAKE_STEW = new FoodComponent.Builder().hunger(6).saturationModifier(0.7f)
            .statusEffect(new StatusEffectInstance(ModEffects.STUFFED, 900, 0), 1f).build();

    public static final FoodComponent SUNFIRE_TOMATO = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200), 1f).build();
    public static final FoodComponent SPICY_SANDWICH = new FoodComponent.Builder().hunger(6).saturationModifier(0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 900, 1), 1f).build();
}
