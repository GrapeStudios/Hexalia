package net.astralya.hexalia.util;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;

public class ModRegistries {

    public static final float SMALL = 0.3F;
    public static final float MEDIUM = 0.5F;
    public static final float LARGE = 0.65F;
    public static final float VERY_LARGE = 0.8F;

    public static void registerModStuff() {
        registerCompostables();
    }

    private static void registerCompostables() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.MANDRAKE_SEEDS, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.SUNFIRE_TOMATO_SEEDS, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RABBAGE_SEEDS, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.CHILLBERRIES, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.GALEBERRIES, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LOTUS_FLOWER, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LOTUS_BLOSSOM, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.COTTONWOOD_LEAVES, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.COTTONWOOD_SAPLING, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.COTTONWOOD_CATKIN, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WILLOW_LEAVES, SMALL);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WILLOW_SAPLING, SMALL);

        CompostingChanceRegistry.INSTANCE.add(ModBlocks.SPIRIT_BLOOM, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.DREAMSHROOM, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.CELESTIAL_BLOOM, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WITHERED_CELESTIAL_BLOOM, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModItems.SIREN_KELP, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.BEGONIA, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.LAVENDER, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.DAHLIA, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.PALE_MUSHROOM, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WITCHWEED, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.GHOST_FERN, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.NIGHTSHADE_BUSH, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.LOURDES, MEDIUM);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.AEGIFLORA, MEDIUM);

        CompostingChanceRegistry.INSTANCE.add(ModItems.MANDRAKE, LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModItems.SUNFIRE_TOMATO, LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RABBAGE, LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModItems.SALTSPROUT, LARGE);

        CompostingChanceRegistry.INSTANCE.add(ModBlocks.MORPHORA, VERY_LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.GRIMSHADE, VERY_LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModItems.NAUTILITE, VERY_LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WINDSONG, VERY_LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.ASTRYLIS, VERY_LARGE);
        CompostingChanceRegistry.INSTANCE.add(ModBlocks.WITHERED_AEGIFLORA, VERY_LARGE);
    }
}