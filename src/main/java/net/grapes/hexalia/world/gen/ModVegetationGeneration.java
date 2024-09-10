package net.grapes.hexalia.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.grapes.hexalia.world.ModPlacedFeatures;
import net.grapes.hexalia.world.biome.ModBiomes;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModVegetationGeneration {
    public static void generateVegetation() {
        // Functional Plants
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP, ModBiomes.ENCHANTED_BAYOU),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.SPIRIT_BLOOM_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.TAIGA, BiomeKeys.SNOWY_TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.CHILLBERRY_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.MUSHROOM_FIELDS, ModBiomes.ENCHANTED_BAYOU),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.DREAMSHROOM_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.SAVANNA),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.WILD_SUNFIRE_TOMATO_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN, ModBiomes.ENCHANTED_BAYOU),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.SIREN_KELP_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.WILD_MANDRAKE_PLACED_KEY);

        // Decorative Plants
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PLAINS),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.HENBANE_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(ModBiomes.ENCHANTED_BAYOU),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.LOTUS_FLOWER_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(ModBiomes.ENCHANTED_BAYOU),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.PALE_MUSHROOM_PLACED_KEY);
    }
}