package net.astralya.hexalia.worldgen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;

public final class ModPlacedFeatures {

    public static final RegistryKey<PlacedFeature> SPIRIT_BLOOM_PLACED_KEY = registerKey("spirit_bloom_placed");
    public static final RegistryKey<PlacedFeature> CHILLBERRY_PLACED_KEY = registerKey("chillberry_placed");
    public static final RegistryKey<PlacedFeature> DREAMSHROOM_PLACED_KEY = registerKey("dreamshroom_placed");
    public static final RegistryKey<PlacedFeature> SIREN_KELP_PLACED_KEY = registerKey("siren_kelp_placed");
    public static final RegistryKey<PlacedFeature> CELESTIAL_BLOOM_PLACED_KEY = registerKey("celestial_bloom_placed");

    public static final RegistryKey<PlacedFeature> WILD_SUNFIRE_TOMATO_PLACED_KEY = registerKey("wild_sunfire_tomato_placed");
    public static final RegistryKey<PlacedFeature> WILD_MANDRAKE_PLACED_KEY = registerKey("wild_mandrake_placed");

    public static final RegistryKey<PlacedFeature> BEGONIA_PLACED_KEY = registerKey("begonia_placed");
    public static final RegistryKey<PlacedFeature> LAVENDER_PLACED_KEY = registerKey("lavender_placed");
    public static final RegistryKey<PlacedFeature> DAHLIA_PLACED_KEY = registerKey("dahlia_placed");

    public static final RegistryKey<PlacedFeature> DARK_OAK_COCOON_PLACED_KEY = registerKey("dark_oak_cocoon_placed");
    public static final RegistryKey<PlacedFeature> COTTONWOOD_PLACED_KEY = registerKey("cottonwood_placed");
    public static final RegistryKey<PlacedFeature> COTTONWOOD_COCOON_PLACED_KEY = registerKey("cottonwood_cocoon_placed");
    public static final RegistryKey<PlacedFeature> WILLOW_PLACED_KEY = registerKey("willow_placed");

    public static final RegistryKey<PlacedFeature> LOTUS_FLOWER_PLACED_KEY = registerKey("lotus_flower_placed");
    public static final RegistryKey<PlacedFeature> PALE_MUSHROOM_PLACED_KEY = registerKey("pale_mushroom_placed");
    public static final RegistryKey<PlacedFeature> WITCHWEED_PLACED_KEY = registerKey("witchweed_placed");
    public static final RegistryKey<PlacedFeature> GHOST_FERN_PLACED_KEY = registerKey("ghost_fern_placed_key");
    public static final RegistryKey<PlacedFeature> NIGHTSHADE_BUSH_PLACED_KEY = registerKey("nightshade_bush_placed_key");

    private ModPlacedFeatures() {}

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configured = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, SPIRIT_BLOOM_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.SPIRIT_BLOOM), rarityPatch(10));
        register(context, CHILLBERRY_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.CHILLBERRY), rarityPatch(10));
        register(context, DREAMSHROOM_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.DREAMSHROOM), rarityPatch(10));
        register(context, SIREN_KELP_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.SIREN_KELP), rarityPatch(10));
        register(context, WILD_SUNFIRE_TOMATO_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.WILD_SUNFIRE_TOMATO), rarityPatch(10));
        register(context, WILD_MANDRAKE_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.WILD_MANDRAKE), rarityPatch(10));
        register(context, GHOST_FERN_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.GHOST_FERN), rarityPatch(10));
        register(context, CELESTIAL_BLOOM_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.CELESTIAL_BLOOM), rarityPatch(10));

        register(context, BEGONIA_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.BEGONIA), rarityPatch(10));
        register(context, LAVENDER_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.LAVENDER), rarityPatch(10));
        register(context, DAHLIA_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.DAHLIA), rarityPatch(10));
        register(context, PALE_MUSHROOM_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.PALE_MUSHROOM), rarityPatch(10));
        register(context, WITCHWEED_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.WITCHWEED), rarityPatch(10));
        register(context, NIGHTSHADE_BUSH_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.NIGHTSHADE_BUSH), rarityPatch(10));
        register(context, LOTUS_FLOWER_PLACED_KEY, configured.getOrThrow(ModConfiguredFeatures.LOTUS_FLOWER), waterSurfacePatch(1));

        register(context, DARK_OAK_COCOON_PLACED_KEY,
                configured.getOrThrow(ModConfiguredFeatures.DARK_OAK_COCOON),
                rareTreePlacement(Blocks.DARK_OAK_SAPLING, 25));

        register(context, COTTONWOOD_PLACED_KEY,
                configured.getOrThrow(ModConfiguredFeatures.COTTONWOOD),
                rareTreePlacement(ModBlocks.COTTONWOOD_SAPLING, 25));

        register(context, COTTONWOOD_COCOON_PLACED_KEY,
                configured.getOrThrow(ModConfiguredFeatures.COTTONWOOD_COCOON),
                rareTreePlacement(ModBlocks.COTTONWOOD_SAPLING, 25));

        register(context, WILLOW_PLACED_KEY,
                configured.getOrThrow(ModConfiguredFeatures.WILLOW),
                rareTreePlacement(ModBlocks.WILLOW_SAPLING, 25));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(HexaliaMod.MODID, name));
    }

    private static void register(Registerable<PlacedFeature> context,
                                 RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static List<PlacementModifier> rarityPatch(int rarity) {
        return List.of(
                RarityFilterPlacementModifier.of(rarity),
                SquarePlacementModifier.of(),
                PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                BiomePlacementModifier.of()
        );
    }

    private static List<PlacementModifier> waterSurfacePatch(int count) {
        return List.of(
                CountPlacementModifier.of(count),
                SquarePlacementModifier.of(),
                PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                BiomePlacementModifier.of()
        );
    }

    private static List<PlacementModifier> rareTreePlacement(Block sapling, int rarity) {
        return VegetationPlacedFeatures.treeModifiersWithWouldSurvive(
                PlacedFeatures.createCountExtraModifier(0, 1.0F / rarity, 1),
                sapling
        );
    }
}