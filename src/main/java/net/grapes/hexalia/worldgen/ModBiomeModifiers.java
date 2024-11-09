package net.grapes.hexalia.worldgen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_SPIRIT_BLOOM = registerKey("add_spirit_bloom");
    public static final ResourceKey<BiomeModifier> ADD_DREAMSHROOM = registerKey("add_dreamshroom");
    public static final ResourceKey<BiomeModifier> ADD_SIREN_KELP = registerKey("add_siren_kelp");
    public static final ResourceKey<BiomeModifier> ADD_CHILLBERRY = registerKey("add_chillberry");
    public static final ResourceKey<BiomeModifier> ADD_WILD_SUNFIRE_TOMATO = registerKey("add_wild_sunfire_tomato");
    public static final ResourceKey<BiomeModifier> ADD_WILD_MANDRAKE = registerKey("add_wild_mandrake");
    public static final ResourceKey<BiomeModifier> ADD_HENBANE = registerKey("add_henbane");
    public static final ResourceKey<BiomeModifier> ADD_DARK_OAK_COCOON = registerKey("add_dark_oak_cocoon");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);
        Optional<Holder.Reference<Biome>> taigaBiome = biomes.get(Biomes.TAIGA);
        Optional<Holder.Reference<Biome>> savannaBiome = biomes.get(Biomes.SAVANNA);
        Optional<Holder.Reference<Biome>> darkForestBiome = biomes.get(Biomes.DARK_FOREST);

        // Functional Plants
        context.register(ADD_SPIRIT_BLOOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_SWAMP),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SPIRIT_BLOOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_DREAMSHROOM, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_DREAMSHROOMS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DREAMSHROOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_SIREN_KELP, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_WATER),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SIREN_KELP_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_CHILLBERRY, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(taigaBiome.get()),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CHILLBERRY_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_WILD_SUNFIRE_TOMATO, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(savannaBiome.get()),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_SUNFIRE_TOMATO_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_WILD_MANDRAKE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_MANDRAKES),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_MANDRAKE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // Decorative Plants
        context.register(ADD_HENBANE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.HENBANE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // Trees
        context.register(ADD_DARK_OAK_COCOON, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(darkForestBiome.get()),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DARK_OAK_COCOON_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(HexaliaMod.MOD_ID, name));
    }
}
