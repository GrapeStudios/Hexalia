package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture,
                              CompletableFuture<TagLookup<Block>> lookupCompletableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, lookupCompletableFuture, HexaliaMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Tags
        this.tag(ItemTags.FOX_FOOD)
                .add(ModItems.CHILLBERRIES.get());

        this.tag(ItemTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get().asItem(), ModBlocks.HENBANE.get().asItem(),
                        ModBlocks.WITCHWEED.get().asItem(), ModBlocks.GHOST_FERN.get().asItem(),
                        ModBlocks.NIGHTSHADE_BUSH.get().asItem());

        this.tag(ItemTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get().asItem(), ModBlocks.HENBANE.get().asItem(),
                        ModBlocks.WITCHWEED.get().asItem(), ModBlocks.GHOST_FERN.get().asItem(),
                        ModBlocks.NIGHTSHADE_BUSH.get().asItem());

        this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.RABBAGE_SEEDS.get(), ModItems.SUNFIRE_TOMATO_SEEDS.get(),
                        ModItems.MANDRAKE_SEEDS.get());

        // Custom Tags
        this.tag(ModTags.Items.COOKED_MEATS)
                .add(Items.COOKED_BEEF, Items.COOKED_CHICKEN,
                        Items.COOKED_MUTTON, Items.COOKED_PORKCHOP,
                        Items.COOKED_RABBIT, Items.COOKED_COD,
                        Items.COOKED_SALMON);

        this.tag(ModTags.Items.CRUSHED_PLANTS)
                .add(ModItems.DREAMSHROOM_PASTE.get(), ModItems.SPIRIT_BLOOM_POWDER.get(),
                        ModItems.SIREN_KELP_PASTE.get(), ModItems.GHOST_FERN_POWDER.get());

        this.tag(ModTags.Items.BREWS)
                .add(ModItems.BREW_OF_SIPHON.get(), ModItems.BREW_OF_HOMESTEAD.get(),
                        ModItems.BREW_OF_SLIMEWALKER.get(), ModItems.BREW_OF_BLOODLUST.get(),
                        ModItems.BREW_OF_SPIKESKIN.get(), ModItems.RUSTIC_BOTTLE.get());

        // Common Tags
        this.tag(ModTags.Items.SALT_DUSTS)
                .add(ModItems.SALT.get());

        this.tag(ModTags.Items.MUSHROOMS)
                .add(ModBlocks.DREAMSHROOM.get().asItem(), ModBlocks.PALE_MUSHROOM.get().asItem());

        this.tag(ModTags.Items.BERRIES)
                .add(ModItems.CHILLBERRIES.get());

        this.tag(ModTags.Items.SEEDS)
                .add(ModItems.MANDRAKE_SEEDS.get(), ModItems.SUNFIRE_TOMATO_SEEDS.get(),
                        ModItems.RABBAGE_SEEDS.get());

        this.tag(ModTags.Items.ORES)
                .add(ModBlocks.SALT_ORE.get().asItem());

        this.tag(ModTags.Items.SALT_ORES)
                .add(ModBlocks.SALT_ORE.get().asItem());

        this.tag(ModTags.Items.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK.get().asItem());
    }

    @Override
    public String getName() {
        return "Item Tags";
    }
}
