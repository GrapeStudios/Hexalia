package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
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
                .add(ModBlocks.SALT_ORE.get(), ModBlocks.SALT_LAMP.get(),
                        ModBlocks.SALT_LAMP.get(), ModBlocks.RUSTIC_OVEN.get(),
                        ModBlocks.SMALL_CAULDRON.get(), ModBlocks.RITUAL_TABLE.get(),
                        ModBlocks.SALT_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.BREW_SHELF.get(), ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SALT_ORE.get(), ModBlocks.SALT_LAMP.get(),
                        ModBlocks.SALT_LAMP.get(), ModBlocks.RUSTIC_OVEN.get(),
                        ModBlocks.SMALL_CAULDRON.get(), ModBlocks.BREW_SHELF.get(),
                        ModBlocks.LOTUS_FLOWER.get(), ModBlocks.RITUAL_TABLE.get(),
                        ModBlocks.SALT_BLOCK.get());

        this.tag(BlockTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get(), ModBlocks.HENBANE.get(),
                        ModBlocks.WITCHWEED.get(), ModBlocks.GHOST_FERN.get(),
                        ModBlocks.NIGHTSHADE_BUSH.get());

        this.tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get(), ModBlocks.HENBANE.get(),
                        ModBlocks.WITCHWEED.get(), ModBlocks.GHOST_FERN.get(),
                        ModBlocks.NIGHTSHADE_BUSH.get());

        this.tag(BlockTags.FROG_PREFER_JUMP_TO)
                .add(ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS)
                .add(ModBlocks.LOTUS_FLOWER.get());

        this.tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.LOTUS_FLOWER.get());

        // Custom Tags
        this.tag(ModTags.Blocks.HEATING_BLOCKS)
                .add(Blocks.MAGMA_BLOCK, Blocks.LAVA,
                        Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE,
                        Blocks.FIRE, Blocks.SOUL_FIRE,
                        ModBlocks.RUSTIC_OVEN.get());

        this.tag(ModTags.Blocks.ATTRACTS_MOTH)
                .add(Blocks.LANTERN, Blocks.SEA_LANTERN,
                        Blocks.SOUL_LANTERN, ModBlocks.SALT_LAMP.get());

        this.tag(ModTags.Blocks.COCOON_LOGS)
                .add(Blocks.DARK_OAK_LOG);

        // Common Tags
        this.tag(ModTags.Blocks.ORES)
                .add(ModBlocks.SALT_ORE.get());

        this.tag(ModTags.Blocks.SALT_ORES)
                .add(ModBlocks.SALT_ORE.get());

        this.tag(ModTags.Blocks.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK.get());
    }
}
