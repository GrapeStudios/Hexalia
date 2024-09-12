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
        // Vanilla Tags
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

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.BREW_SHELF)
                .add(ModBlocks.LOTUS_FLOWER);

        getOrCreateTagBuilder(BlockTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM, ModBlocks.HENBANE,
                        ModBlocks.WITCHWEED, ModBlocks.GHOST_FERN);

        getOrCreateTagBuilder(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM, ModBlocks.HENBANE,
                        ModBlocks.WITCHWEED, ModBlocks.GHOST_FERN);

        getOrCreateTagBuilder(BlockTags.FROG_PREFER_JUMP_TO)
                .add(ModBlocks.LOTUS_FLOWER);

        getOrCreateTagBuilder(BlockTags.INSIDE_STEP_SOUND_BLOCKS)
                .add(ModBlocks.LOTUS_FLOWER);

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.LOTUS_FLOWER);

        // Tags for Tree Related Blocks
        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .addTag(ModTags.Blocks.COTTONWOOD_LOGS)
                .addTag(ModTags.Blocks.WILLOW_LOGS);
        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.COTTONWOOD_LEAVES, ModBlocks.WILLOW_LEAVES);

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.COTTONWOOD_PLANKS, ModBlocks.WILLOW_PLANKS);
        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.COTTONWOOD_STAIRS, ModBlocks.WILLOW_STAIRS);
        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.COTTONWOOD_SLAB, ModBlocks.WILLOW_SLAB);
        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.COTTONWOOD_DOOR, ModBlocks.WILLOW_DOOR);
        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.COTTONWOOD_BUTTON, ModBlocks.WILLOW_BUTTON);
        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.COTTONWOOD_PRESSURE_PLATE, ModBlocks.WILLOW_PRESSURE_PLATE);
        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.COTTONWOOD_TRAPDOOR, ModBlocks.WILLOW_TRAPDOOR);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES)
                .add(ModBlocks.COTTONWOOD_FENCE_GATE, ModBlocks.WILLOW_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.COTTONWOOD_FENCE, ModBlocks.WILLOW_FENCE);

        getOrCreateTagBuilder(BlockTags.SIGNS)
                .add(ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_WALL_SIGN, ModBlocks.COTTONWOOD_SIGN,
                        ModBlocks.WILLOW_WALL_SIGN);
        getOrCreateTagBuilder(BlockTags.ALL_HANGING_SIGNS)
                .add(ModBlocks.COTTONWOOD_WALL_SIGN, ModBlocks.COTTONWOOD_HANGING_WALL_SIGN,
                        ModBlocks.WILLOW_WALL_SIGN, ModBlocks.WILLOW_HANGING_WALL_SIGN);

        // Custom Tags
        getOrCreateTagBuilder(ModTags.Blocks.HEATING_BLOCKS)
                .add(Blocks.MAGMA_BLOCK, Blocks.LAVA,
                        Blocks.CAMPFIRE, Blocks.FIRE, ModBlocks.RUSTIC_OVEN);

        getOrCreateTagBuilder(ModTags.Blocks.ATTRACTS_MOTH)
                .add(Blocks.LANTERN, Blocks.SEA_LANTERN,
                        Blocks.SOUL_LANTERN, ModBlocks.SALT_LAMP);

        getOrCreateTagBuilder(ModTags.Blocks.COCOON_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG, Blocks.DARK_OAK_LOG);

        getOrCreateTagBuilder(ModTags.Blocks.COTTONWOOD_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG, ModBlocks.STRIPPED_COTTONWOOD_LOG,
                        ModBlocks.COTTONWOOD_WOOD, ModBlocks.STRIPPED_COTTONWOOD_WOOD);

        getOrCreateTagBuilder(ModTags.Blocks.WILLOW_LOGS)
                .add(ModBlocks.WILLOW_LOG, ModBlocks.STRIPPED_WILLOW_LOG,
                        ModBlocks.WILLOW_WOOD, ModBlocks.STRIPPED_WILLOW_WOOD);

        // Common Tags
        getOrCreateTagBuilder(ModTags.Blocks.ORES)
                .add(ModBlocks.SALT_ORE);

        getOrCreateTagBuilder(ModTags.Blocks.SALT_ORES)
                .add(ModBlocks.SALT_ORE);

        getOrCreateTagBuilder(ModTags.Blocks.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK);
    }
}
