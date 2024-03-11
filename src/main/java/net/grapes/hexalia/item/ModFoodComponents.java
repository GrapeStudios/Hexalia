package net.grapes.hexalia.item;

import net.grapes.hexalia.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent CHILLBERRIES = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100), 1f).build();
    public static final FoodComponent CHILLBERRY_CUPCAKE = new FoodComponent.Builder().hunger(6).saturationModifier(0.8f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 1), 1f).build();

    public static final FoodComponent MANDRAKE_STEW = new FoodComponent.Builder().hunger(6).saturationModifier(0.7f)
            .statusEffect(new StatusEffectInstance(ModEffects.STUFFED, 1200, 0), 1f) .build();

    public static final FoodComponent SUNFIRE_TOMATO = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).build();

    public static final FoodComponent SPICY_SANDWICH = new FoodComponent.Builder().hunger(6).saturationModifier(0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 0), 1f).build();
}
