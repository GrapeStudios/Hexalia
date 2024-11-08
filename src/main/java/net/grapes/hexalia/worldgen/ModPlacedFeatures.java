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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> COTTONWOOD_PLACED_KEY = registerKey("cottonwood_placed");
    public static final ResourceKey<PlacedFeature> WILLOW_PLACED_KEY = registerKey("willow_placed");

    public static final void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);


        // Trees Generation
        register(context, COTTONWOOD_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.COTTONWOOD_KEY),
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


