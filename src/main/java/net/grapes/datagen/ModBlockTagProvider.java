package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.util.ModTags;
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
        // Tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.SALT_ORE)
                .add(ModBlocks.SALT_BLOCK)
                .add(ModBlocks.SALT_LAMP)
                .add(ModBlocks.RUSTIC_OVEN)
                .add(ModBlocks.SMALL_CAULDRON)
                .add(ModBlocks.RITUAL_TABLE);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SALT_ORE)
                .add(ModBlocks.SALT_BLOCK)
                .add(ModBlocks.SALT_LAMP)
                .add(ModBlocks.SMALL_CAULDRON)
                .add(ModBlocks.RUSTIC_OVEN)
                .add(ModBlocks.RITUAL_TABLE);

        /* .addTag(ModTags.Blocks.COTTONWOOD_LOGS)
                .add(ModBlocks.COTTONWOOD_PLANKS)
                .add(ModBlocks.COTTONWOOD_STAIRS)
                .add(ModBlocks.COTTONWOOD_SLAB)
                .add(ModBlocks.COTTONWOOD_FENCE_GATE)
                .add(ModBlocks.COTTONWOOD_FENCE)
                .add(ModBlocks.COTTONWOOD_TRAPDOOR)
                .add(ModBlocks.COTTONWOOD_DOOR)
                .add(ModBlocks.COTTONWOOD_SIGN)
                .add(ModBlocks.COTTONWOOD_HANGING_SIGN)
                .add(ModBlocks.COTTONWOOD_HANGING_WALL_SIGN)
                .add(ModBlocks.COTTONWOOD_WALL_SIGN);*/

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.BREW_SHELF);

        getOrCreateTagBuilder(BlockTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM, ModBlocks.HENBANE);

        getOrCreateTagBuilder(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM, ModBlocks.HENBANE);

        // Tags for Tree Related Blocks
        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .addTag(ModTags.Blocks.COTTONWOOD_LOGS);
        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.COTTONWOOD_LEAVES);
        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.COTTONWOOD_PLANKS);
        getOrCreateTagBuilder(ModTags.Blocks.COCOON_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG, Blocks.DARK_OAK_LOG);

        // Custom Tags
        getOrCreateTagBuilder(ModTags.Blocks.HEATING_BLOCKS)
                .add(Blocks.MAGMA_BLOCK, Blocks.LAVA,
                        Blocks.CAMPFIRE, Blocks.FIRE, ModBlocks.RUSTIC_OVEN);

        getOrCreateTagBuilder(ModTags.Blocks.ATTRACTS_MOTH)
                .add(Blocks.LANTERN, Blocks.SEA_LANTERN,
                        Blocks.SOUL_LANTERN, ModBlocks.SALT_LAMP);

        getOrCreateTagBuilder(ModTags.Blocks.COTTONWOOD_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG, ModBlocks.STRIPPED_COTTONWOOD_LOG,
                        ModBlocks.COTTONWOOD_WOOD, ModBlocks.STRIPPED_COTTONWOOD_WOOD);

        // Common Tags
        getOrCreateTagBuilder(ModTags.Blocks.ORES)
                .add(ModBlocks.SALT_ORE);

        getOrCreateTagBuilder(ModTags.Blocks.SALT_ORES)
                .add(ModBlocks.SALT_ORE);

        getOrCreateTagBuilder(ModTags.Blocks.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK);
    }
}
