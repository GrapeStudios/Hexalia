package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.SALT_ORE)
                .add(ModBlocks.SALT_BLOCK)
                .add(ModBlocks.SALT_LAMP)
                .add(ModBlocks.RUSTIC_OVEN)
                .add(ModBlocks.RITUAL_TABLE);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SALT_ORE)
                .add(ModBlocks.SALT_BLOCK)
                .add(ModBlocks.SALT_LAMP);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SMALL_CAULDRON)
                .add(ModBlocks.RUSTIC_OVEN)
                .add(ModBlocks.RITUAL_TABLE);

        getOrCreateTagBuilder(ModTags.Blocks.HEATING_BLOCK)
                .add(Blocks.MAGMA_BLOCK, Blocks.LAVA,
                        Blocks.CAMPFIRE, Blocks.FIRE, ModBlocks.RUSTIC_OVEN);
    }
}
