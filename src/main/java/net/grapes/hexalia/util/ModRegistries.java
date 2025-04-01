package net.grapes.hexalia.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;

public class ModRegistries {
    public static void registerModStuff() {
        registerCompostables();
        registerEntityAttributes();
    }

    public static final float SMALL = 0.3f;
    public static final float MEDIUM = 0.5f;
    public static final float LARGE = 0.65f;

    private static void registerCompostables() {
        // Seeds and small plants (30% chance)
        registerCompostable(ModItems.MANDRAKE_SEEDS, SMALL);
        registerCompostable(ModItems.SUNFIRE_TOMATO_SEEDS, SMALL);
        registerCompostable(ModItems.RABBAGE_SEEDS, SMALL);
        registerCompostable(ModItems.CHILLBERRIES, SMALL);
        registerCompostable(ModItems.LOTUS_FLOWER, SMALL);
        registerCompostable(ModItems.DUCKWEED, SMALL);
        registerCompostable(ModItems.MOON_BERRIES, SMALL);

        // Medium-value items (50% chance)
        registerCompostable(ModItems.SIREN_KELP, MEDIUM);

        // High-value crops (60% chance)
        registerCompostable(ModItems.MANDRAKE, LARGE);
        registerCompostable(ModItems.SUNFIRE_TOMATO, LARGE);
        registerCompostable(ModItems.RABBAGE, LARGE);
    }

    private static void registerCompostable(Item item, float chance) {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), chance);
    }

    private static void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(
                ModEntities.SILK_MOTH,
                SilkMothEntity.setAttributes()
        );
    }
}