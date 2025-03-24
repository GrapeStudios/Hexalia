package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.util.ModTags;
import net.grapes.hexalia.worldgen.biome.ModBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HexaliaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Biome Tags
        this.tag(ModTags.Biomes.HAS_MANDRAKES)
                .add(Biomes.FOREST)
                .add(Biomes.BIRCH_FOREST);
        this.tag(ModTags.Biomes.HAS_DREAMSHROOMS)
                .add(Biomes.OLD_GROWTH_PINE_TAIGA)
                .addOptional(ModBiomes.ENCHANTED_BAYOU.location())
                .add(Biomes.MUSHROOM_FIELDS);
        this.tag(Tags.Biomes.IS_SWAMP)
                .addOptional(ModBiomes.ENCHANTED_BAYOU.location());
        this.tag(ModTags.Biomes.HAS_SIREN_KELP)
                .addTag(BiomeTags.IS_OCEAN)
                .addOptional(ModBiomes.ENCHANTED_BAYOU.location());
    }
}
