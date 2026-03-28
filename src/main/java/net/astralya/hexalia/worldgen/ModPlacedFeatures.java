package net.astralya.hexalia.worldgen;

import java.util.List;
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
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public final class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> SPIRIT_BLOOM_PLACED = registerKey("spirit_bloom_placed");
    public static final RegistryKey<PlacedFeature> DREAMSHROOM_PLACED = registerKey("dreamshroom_placed");
    public static final RegistryKey<PlacedFeature> SIREN_KELP_PLACED = registerKey("siren_kelp_placed");
    public static final RegistryKey<PlacedFeature> CHILLBERRY_PLACED = registerKey("chillberry_placed");
    public static final RegistryKey<PlacedFeature> WILD_SUNFIRE_TOMATO_PLACED = registerKey("wild_sunfire_tomato_placed");
    public static final RegistryKey<PlacedFeature> WILD_MANDRAKE_PLACED = registerKey("wild_mandrake_placed");
    public static final RegistryKey<PlacedFeature> GHOST_FERN_PLACED = registerKey("ghost_fern_placed");
    public static final RegistryKey<PlacedFeature> CELESTIAL_BLOOM_PLACED = registerKey("celestial_bloom_placed");
    public static final RegistryKey<PlacedFeature> SALTSPROUT_PLACED = registerKey("saltsprout_placed");
    public static final RegistryKey<PlacedFeature> BEGONIA_PLACED = registerKey("begonia_placed");
    public static final RegistryKey<PlacedFeature> LAVENDER_PLACED = registerKey("lavender_placed");
    public static final RegistryKey<PlacedFeature> DAHLIA_PLACED = registerKey("dahlia_placed");
    public static final RegistryKey<PlacedFeature> LOTUS_FLOWER_PLACED = registerKey("lotus_flower_placed");
    public static final RegistryKey<PlacedFeature> PALE_MUSHROOM_PLACED = registerKey("pale_mushroom_placed");
    public static final RegistryKey<PlacedFeature> WITCHWEED_PLACED = registerKey("witchweed_placed");
    public static final RegistryKey<PlacedFeature> NIGHTSHADE_BUSH_PLACED = registerKey("nightshade_bush_placed");
    public static final RegistryKey<PlacedFeature> COTTONWOOD_PLACED = registerKey("cottonwood_placed");
    public static final RegistryKey<PlacedFeature> WILLOW_PLACED = registerKey("willow_placed");
    public static final RegistryKey<PlacedFeature> DARK_OAK_COCOON_PLACED = registerKey("dark_oak_cocoon_placed");

    private ModPlacedFeatures() {
    }

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configured = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, SPIRIT_BLOOM_PLACED, configured.getOrThrow(ModConfiguredFeatures.SPIRIT_BLOOM), rarityPatch(4));
        register(context, DREAMSHROOM_PLACED, configured.getOrThrow(ModConfiguredFeatures.DREAMSHROOM), rarityPatch(4));
        register(context, SIREN_KELP_PLACED, configured.getOrThrow(ModConfiguredFeatures.SIREN_KELP), waterSurfacePatch(4));
        register(context, GHOST_FERN_PLACED, configured.getOrThrow(ModConfiguredFeatures.GHOST_FERN), rarityPatch(4));
        register(context, CELESTIAL_BLOOM_PLACED, configured.getOrThrow(ModConfiguredFeatures.CELESTIAL_BLOOM), rarityPatch(4));
        register(context, WITCHWEED_PLACED, configured.getOrThrow(ModConfiguredFeatures.WITCHWEED), rarityPatch(4));
        register(context, CHILLBERRY_PLACED, configured.getOrThrow(ModConfiguredFeatures.CHILLBERRY), rarityPatch(6));
        register(context, BEGONIA_PLACED, configured.getOrThrow(ModConfiguredFeatures.BEGONIA), rarityPatch(6));
        register(context, LAVENDER_PLACED, configured.getOrThrow(ModConfiguredFeatures.LAVENDER), rarityPatch(6));
        register(context, DAHLIA_PLACED, configured.getOrThrow(ModConfiguredFeatures.DAHLIA), rarityPatch(6));
        register(context, PALE_MUSHROOM_PLACED, configured.getOrThrow(ModConfiguredFeatures.PALE_MUSHROOM), rarityPatch(6));
        register(context, WILD_SUNFIRE_TOMATO_PLACED, configured.getOrThrow(ModConfiguredFeatures.WILD_SUNFIRE_TOMATO), rarityPatch(8));
        register(context, NIGHTSHADE_BUSH_PLACED, configured.getOrThrow(ModConfiguredFeatures.NIGHTSHADE_BUSH), rarityPatch(8));
        register(context, WILD_MANDRAKE_PLACED, configured.getOrThrow(ModConfiguredFeatures.WILD_MANDRAKE), rarityPatch(10));
        register(context, SALTSPROUT_PLACED, configured.getOrThrow(ModConfiguredFeatures.SALTSPROUT), rarityPatch(20));
        register(context, LOTUS_FLOWER_PLACED, configured.getOrThrow(ModConfiguredFeatures.LOTUS_FLOWER), rarityPatch(30));

        register(context, COTTONWOOD_PLACED, configured.getOrThrow(ModConfiguredFeatures.COTTONWOOD), rareTreePlacement(ModBlocks.COTTONWOOD_SAPLING, 25));
        register(context, WILLOW_PLACED, configured.getOrThrow(ModConfiguredFeatures.WILLOW), rareTreePlacement(ModBlocks.WILLOW_SAPLING, 25));
        register(context, DARK_OAK_COCOON_PLACED, configured.getOrThrow(ModConfiguredFeatures.DARK_OAK_COCOON), rareTreePlacement(Blocks.DARK_OAK_SAPLING, 25));
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
                net.minecraft.world.gen.placementmodifier.CountPlacementModifier.of(count),
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

    private static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(HexaliaMod.MODID, name));
    }

    private static void register(
            Registerable<PlacedFeature> context,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers
    ) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}