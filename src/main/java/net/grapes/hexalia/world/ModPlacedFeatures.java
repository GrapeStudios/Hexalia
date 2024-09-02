package net.grapes.hexalia.world;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {

    public static final RegistryKey<PlacedFeature> SALT_ORE_PLACED_KEY = registerKey("salt_ore_placed");
    public static final RegistryKey<PlacedFeature> SPIRIT_BLOOM_PLACED_KEY = registerKey("spirit_bloom_placed");
    public static final RegistryKey<PlacedFeature> CHILLBERRY_PLACED_KEY = registerKey("chillberry_placed");
    public static final RegistryKey<PlacedFeature> DREAMSHROOM_PLACED_KEY = registerKey("dreamshroom_placed");
    public static final RegistryKey<PlacedFeature> SIREN_KELP_PLACED_KEY = registerKey("siren_kelp_placed");
    public static final RegistryKey<PlacedFeature> WILD_SUNFIRE_TOMATO_PLACED_KEY = registerKey("wild_sunfire_tomato_placed");
    public static final RegistryKey<PlacedFeature> WILD_MANDRAKE_PLACED_KEY = registerKey("wild_mandrake_placed");
    public static final RegistryKey<PlacedFeature> HENBANE_PLACED_KEY = registerKey("henbane_placed");
    public static final RegistryKey<PlacedFeature> DARK_OAK_COCOON_PLACED_KEY = registerKey("dark_oak_cocoon_placed");
    public static final RegistryKey<PlacedFeature> COTTONWOOD_PLACED_KEY = registerKey("cottonwood_placed");
    public static final RegistryKey<PlacedFeature> COTTONWOOD_COCOON_PLACED_KEY = registerKey("cottonwood_cocoon_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, SALT_ORE_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.SALT_ORE_KEY),
                ModOrePlacement.modifiersWithCount(8,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));

        register(context, SPIRIT_BLOOM_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.SPIRIT_BLOOM_KEY),
                RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, CHILLBERRY_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.CHILLBERRY_KEY),
                RarityFilterPlacementModifier.of(15), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, DREAMSHROOM_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DREAMSHROOM_KEY),
                RarityFilterPlacementModifier.of(25), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, WILD_SUNFIRE_TOMATO_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WILD_SUNFIRE_TOMATO_KEY),
                RarityFilterPlacementModifier.of(10), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, SIREN_KELP_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.SIREN_KELP_KEY),
                CountPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, WILD_MANDRAKE_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WILD_MANDRAKE_KEY),
                RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, HENBANE_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.HENBANE),
                RarityFilterPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

        register(context, DARK_OAK_COCOON_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DARK_OAK_COCOON_KEY),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(1, 0.1f, 1), Blocks.DARK_OAK_SAPLING));

        register(context, COTTONWOOD_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.COTTONWOOD_KEY),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive
                        (PlacedFeatures.createCountExtraModifier(1, 0.1f, 2), ModBlocks.COTTONWOOD_SAPLING));

        register(context, COTTONWOOD_COCOON_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.COTTONWOOD_COCOON_KEY),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(1, 0.1f, 1), ModBlocks.COTTONWOOD_SAPLING));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(HexaliaMod.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}
