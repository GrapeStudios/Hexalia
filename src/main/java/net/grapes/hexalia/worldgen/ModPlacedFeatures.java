package net.grapes.hexalia.worldgen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SPIRIT_BLOOM_PLACED_KEY = registerKey("spirit_bloom_placed");
    public static final ResourceKey<PlacedFeature> DREAMSHROOM_PLACED_KEY = registerKey("dreamshroom_placed");
    public static final ResourceKey<PlacedFeature> SIREN_KELP_PLACED_KEY = registerKey("siren_kelp_placed");
    public static final ResourceKey<PlacedFeature> CHILLBERRY_PLACED_KEY = registerKey("chillberry_placed");
    public static final ResourceKey<PlacedFeature> WILD_SUNFIRE_TOMATO_PLACED_KEY = registerKey("wild_sunfire_tomato_placed");
    public static final ResourceKey<PlacedFeature> WILD_MANDRAKE_PLACED_KEY = registerKey("wild_mandrake_placed");
    public static final ResourceKey<PlacedFeature> HENBANE_PLACED_KEY = registerKey("henbane_placed");

    public static final ResourceKey<PlacedFeature> COTTONWOOD_PLACED_KEY = registerKey("cottonwood_placed");
    public static final ResourceKey<PlacedFeature> WILLOW_PLACED_KEY = registerKey("willow_placed");
    public static final ResourceKey<PlacedFeature> COTTONWOOD_COCOON_PLACED_KEY = registerKey("cottonwood_cocoon_placed");
    public static final ResourceKey<PlacedFeature> DARK_OAK_COCOON_PLACED_KEY = registerKey("dark_oak_cocoon_placed");

    public static final ResourceKey<PlacedFeature> LOTUS_FLOWER_PLACED_KEY = registerKey("lotus_flower_placed");
    public static final ResourceKey<PlacedFeature> PALE_MUSHROOM_PLACED_KEY = registerKey("pale_mushroom_placed");
    public static final ResourceKey<PlacedFeature> WITCHWEED_PLACED_KEY = registerKey("witchweed_placed");
    public static final ResourceKey<PlacedFeature> GHOST_FERN_PLACED_KEY = registerKey("ghost_fern_placed_key");
    public static final ResourceKey<PlacedFeature> HEXED_BULRUSH_PLACED_KEY = registerKey("hexed_bulrush_placed_key");
    public static final ResourceKey<PlacedFeature> NIGHTSHADE_BUSH_PLACED_KEY = registerKey("nightshade_bush_placed_key");
    public static final ResourceKey<PlacedFeature> DUCKWEED_PLACED_KEY = registerKey("duckweed_placed_key");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Functional Plants
        register(context, SPIRIT_BLOOM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SPIRIT_BLOOM_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, DREAMSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DREAMSHROOM_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, SIREN_KELP_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SIREN_KELP_KEY),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BiomeFilter.biome()));

        register(context, CHILLBERRY_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CHILLBERRY_KEY),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, WILD_SUNFIRE_TOMATO_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILD_SUNFIRE_TOMATO_KEY),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, WILD_MANDRAKE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILD_MANDRAKE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, GHOST_FERN_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GHOST_FERN_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        // Decorative Plants
        register(context, HENBANE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.HENBANE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, LOTUS_FLOWER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LOTUS_FLOWER_KEY),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, PALE_MUSHROOM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PALE_MUSHROOM_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, WITCHWEED_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WITCHWEED_KEY),
                List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, HEXED_BULRUSH_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.HEXED_BULRUSH_KEY),
                List.of(CountPlacement.of(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()));

        register(context, NIGHTSHADE_BUSH_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.NIGHTSHADE_BUSH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                        BiomeFilter.biome()));

        register(context, DUCKWEED_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DUCKWEED_KEY),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        // Trees Generation
        register(context, DARK_OAK_COCOON_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DARK_OAK_COCOON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1),
                        Blocks.DARK_OAK_SAPLING));

        register(context, COTTONWOOD_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.COTTONWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 1),
                        ModBlocks.COTTONWOOD_SAPLING.get()));

        register(context, COTTONWOOD_COCOON_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.COTTONWOOD_COCOON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, 0.1f, 1),
                        ModBlocks.COTTONWOOD_SAPLING.get()));

        register(context, WILLOW_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILLOW_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(10, 0.1f, 1),
                        ModBlocks.WILLOW_SAPLING.get()));
    }


    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(HexaliaMod.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}


