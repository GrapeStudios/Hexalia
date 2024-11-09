package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
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
                .add(Biomes.MUSHROOM_FIELDS);
    }
}
