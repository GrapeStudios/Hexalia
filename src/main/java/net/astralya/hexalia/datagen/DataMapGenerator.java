package net.astralya.hexalia.datagen;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class DataMapGenerator extends DataMapProvider {
    protected DataMapGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.COMPOSTABLES)
                // 30%
                .add(ModItems.MANDRAKE_SEEDS.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.SUNFIRE_TOMATO_SEEDS.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.RABBAGE_SEEDS.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.CHILLBERRIES.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.GALEBERRIES.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.LOTUS_FLOWER.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.COTTONWOOD_LEAVES.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.COTTONWOOD_SAPLING.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WILLOW_LEAVES.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WILLOW_SAPLING.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)

                // 50%
                .add(ModBlocks.SPIRIT_BLOOM.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.DREAMSHROOM.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.CELESTIAL_BLOOM.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WITHERED_CELESTIAL_BLOOM.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.SIREN_KELP.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.BEGONIA.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.LAVENDER.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.DAHLIA.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.PALE_MUSHROOM.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WITCHWEED.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.GHOST_FERN.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.NIGHTSHADE_BUSH.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)

                // 60%
                .add(ModItems.MANDRAKE.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.SUNFIRE_TOMATO.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.RABBAGE.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.SALTSPROUT.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)

                // 80%
                .add(ModBlocks.MORPHORA.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.GRIMSHADE.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModItems.NAUTILITE.get().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WINDSONG.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.ASTRYLIS.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.LOURDES.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.AEGIFLORA.get().asItem().builtInRegistryHolder(), new Compostable(0.3f), false)
                .add(ModBlocks.WITHERED_AEGIFLORA.asItem().builtInRegistryHolder(), new Compostable(0.3f), false);
    }
}
