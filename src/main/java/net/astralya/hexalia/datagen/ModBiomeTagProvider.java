package net.astralya.hexalia.datagen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HexaliaMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Biome Tags
        this.tag(ModTags.Biomes.HAS_MANDRAKES)
                .add(Biomes.FOREST)
                .add(Biomes.BIRCH_FOREST);
        this.tag(ModTags.Biomes.HAS_DREAMSHROOMS)
                .add(Biomes.OLD_GROWTH_PINE_TAIGA)
                .add(Biomes.MUSHROOM_FIELDS);
        this.tag(ModTags.Biomes.HAS_SIREN_KELP)
                .addTag(BiomeTags.IS_OCEAN);
        this.tag(ModTags.Biomes.HAS_GHOST_FERNS)
                .add(Biomes.DARK_FOREST);
        this.tag(ModTags.Biomes.HAS_DECORATIVE_FLOWERS)
                .add(Biomes.SUNFLOWER_PLAINS)
                .add(Biomes.PLAINS);
        this.tag(ModTags.Biomes.HAS_SWAMP_VEGETATION)
                .add(Biomes.MANGROVE_SWAMP)
                .add(Biomes.SWAMP);
    }
}
