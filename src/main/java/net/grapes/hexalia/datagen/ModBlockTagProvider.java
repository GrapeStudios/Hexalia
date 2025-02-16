package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HexaliaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        // Common Tags
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SALT_LAMP.get(),
                        ModBlocks.SALT_LAMP.get(), ModBlocks.RUSTIC_OVEN.get(),
                        ModBlocks.SMALL_CAULDRON.get(), ModBlocks.RITUAL_TABLE.get(),
                        ModBlocks.SALT_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.BREW_SHELF.get(), ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SALT_LAMP.get(),
                        ModBlocks.SALT_LAMP.get(), ModBlocks.RUSTIC_OVEN.get(),
                        ModBlocks.SMALL_CAULDRON.get(), ModBlocks.BREW_SHELF.get(),
                        ModBlocks.LOTUS_FLOWER.get(), ModBlocks.RITUAL_TABLE.get(),
                        ModBlocks.SALT_BLOCK.get());

        this.tag(BlockTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get(), ModBlocks.HENBANE.get(),
                        ModBlocks.WITCHWEED.get(), ModBlocks.GHOST_FERN.get(),
                        ModBlocks.NIGHTSHADE_BUSH.get(), ModBlocks.BEGONIA.get(),
                        ModBlocks.LAVENDER.get());

        this.tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get(), ModBlocks.HENBANE.get(),
                        ModBlocks.WITCHWEED.get(), ModBlocks.GHOST_FERN.get(),
                        ModBlocks.NIGHTSHADE_BUSH.get(), ModBlocks.BEGONIA.get(),
                        ModBlocks.LAVENDER.get());

        this.tag(BlockTags.FROG_PREFER_JUMP_TO)
                .add(ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS)
                .add(ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.CROPS)
                .add(ModBlocks.SUNFIRE_TOMATO_CROP.get(),
                        ModBlocks.MANDRAKE_CROP.get(), ModBlocks.RABBAGE_CROP.get(),
                        ModBlocks.SALTSPROUT.get(), ModBlocks.CHILLBERRY_BUSH.get());

        this.tag(BlockTags.SAPLINGS)
                .add(ModBlocks.WILLOW_SAPLING.get(), ModBlocks.COTTONWOOD_SAPLING.get());

        // Custom Tags
        this.tag(ModTags.Blocks.HEATING_BLOCKS)
                .add(Blocks.MAGMA_BLOCK, Blocks.LAVA,
                        Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE,
                        Blocks.FIRE, Blocks.SOUL_FIRE,
                        ModBlocks.RUSTIC_OVEN.get());

        this.tag(ModTags.Blocks.ATTRACTS_MOTH)
                .add(Blocks.LANTERN, Blocks.SEA_LANTERN,
                        Blocks.SOUL_LANTERN, ModBlocks.SALT_LAMP.get(),
                        Blocks.END_ROD, ModBlocks.LUNAR_LILY.get());

        this.tag(ModTags.Blocks.COCOON_LOGS)
                .add(Blocks.DARK_OAK_LOG, ModBlocks.COTTONWOOD_LOG.get());

        // Common Tags
        this.tag(ModTags.Blocks.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK.get());

        // Wood-related Tags
        this.tag(BlockTags.LOGS_THAT_BURN)
                .addTag(ModTags.Blocks.COTTONWOOD_LOGS)
                .addTag(ModTags.Blocks.WILLOW_LOGS);

        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.COTTONWOOD_LEAVES.get(), ModBlocks.WILLOW_LEAVES.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.COTTONWOOD_PLANKS.get(), ModBlocks.WILLOW_PLANKS.get());
        this.tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.COTTONWOOD_STAIRS.get(), ModBlocks.WILLOW_STAIRS.get());
        this.tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.COTTONWOOD_SLAB.get(), ModBlocks.WILLOW_SLAB.get());
        this.tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.COTTONWOOD_DOOR.get(), ModBlocks.WILLOW_DOOR.get());
        this.tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.COTTONWOOD_BUTTON.get(), ModBlocks.WILLOW_BUTTON.get());
        this.tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), ModBlocks.WILLOW_PRESSURE_PLATE.get());
        this.tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.COTTONWOOD_TRAPDOOR.get(), ModBlocks.WILLOW_TRAPDOOR.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.COTTONWOOD_FENCE_GATE.get(), ModBlocks.WILLOW_FENCE_GATE.get());
        this.tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.COTTONWOOD_FENCE.get(), ModBlocks.WILLOW_FENCE.get());

        this.tag(BlockTags.SIGNS)
                .add(ModBlocks.COTTONWOOD_SIGN.get(), ModBlocks.COTTONWOOD_WALL_SIGN.get(), ModBlocks.COTTONWOOD_SIGN.get(),
                        ModBlocks.WILLOW_WALL_SIGN.get());
        this.tag(BlockTags.ALL_HANGING_SIGNS)
                .add(ModBlocks.COTTONWOOD_WALL_SIGN.get(), ModBlocks.COTTONWOOD_HANGING_WALL_SIGN.get(),
                        ModBlocks.WILLOW_WALL_SIGN.get(), ModBlocks.WILLOW_HANGING_WALL_SIGN.get());

        this.tag(ModTags.Blocks.COTTONWOOD_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG.get(), ModBlocks.STRIPPED_COTTONWOOD_LOG.get(),
                        ModBlocks.COTTONWOOD_WOOD.get(), ModBlocks.STRIPPED_COTTONWOOD_WOOD.get());

        this.tag(ModTags.Blocks.WILLOW_LOGS)
                .add(ModBlocks.WILLOW_LOG.get(), ModBlocks.STRIPPED_WILLOW_LOG.get(),
                        ModBlocks.WILLOW_WOOD.get(), ModBlocks.STRIPPED_WILLOW_WOOD.get(),
                        ModBlocks.WILLOW_MOSSY_WOOD.get());

    }
}
