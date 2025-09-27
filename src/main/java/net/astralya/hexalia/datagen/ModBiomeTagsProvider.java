package net.astralya.hexalia.datagen;

import net.astralya.hexalia.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends FabricTagProvider<Biome> {

    public ModBiomeTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        getOrCreateTagBuilder(ModTags.Biomes.HAS_MANDRAKES)
                .add(BiomeKeys.FOREST)
                .add(BiomeKeys.BIRCH_FOREST);

        getOrCreateTagBuilder(ModTags.Biomes.HAS_DREAMSHROOMS)
                .add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)
                .add(BiomeKeys.MUSHROOM_FIELDS);

        getOrCreateTagBuilder(ModTags.Biomes.HAS_SIREN_KELP)
                .addOptionalTag(BiomeTags.IS_OCEAN);

        getOrCreateTagBuilder(ModTags.Biomes.HAS_GHOST_FERNS)
                .add(BiomeKeys.DARK_FOREST);

        getOrCreateTagBuilder(ModTags.Biomes.HAS_DECORATIVE_FLOWERS)
                .add(BiomeKeys.SUNFLOWER_PLAINS)
                .add(BiomeKeys.PLAINS);

        getOrCreateTagBuilder(ModTags.Biomes.HAS_SWAMP_VEGETATION)
                .add(BiomeKeys.MANGROVE_SWAMP)
                .add(BiomeKeys.SWAMP);
    }
}