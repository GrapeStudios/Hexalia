package net.astralya.hexalia.worldgen.gen;

import net.astralya.hexalia.util.ModTags;
import net.astralya.hexalia.worldgen.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class ModNaturalGeneration {

    private ModNaturalGeneration() {}

    public static void registerNaturalGeneration () {
        addVegetation();
        addTrees();
    }

    private static void addVegetation() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_SWAMP_VEGETATION),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.SPIRIT_BLOOM_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_DREAMSHROOMS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.DREAMSHROOM_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_SIREN_KELP),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.SIREN_KELP_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.CHILLBERRY_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.SAVANNA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.WILD_SUNFIRE_TOMATO_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_MANDRAKES),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.WILD_MANDRAKE_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.MEADOW),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.CELESTIAL_BLOOM_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_GHOST_FERNS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.GHOST_FERN_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_SWAMP_VEGETATION),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.LOTUS_FLOWER_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.tag(ModTags.Biomes.HAS_DECORATIVE_FLOWERS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.BEGONIA_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.LAVENDER_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.FLOWER_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.DAHLIA_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.FLOWER_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.WITCHWEED_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.PALE_MUSHROOM_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.NIGHTSHADE_BUSH_PLACED_KEY
        );
    }

    private static void addTrees() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.DARK_OAK_COCOON_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.SWAMP),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.COTTONWOOD_PLACED_KEY
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.MANGROVE_SWAMP),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.WILLOW_PLACED_KEY
        );
    }
}
