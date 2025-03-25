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
                .add(ModItems.CHILLBERRIES.get(), ModItems.MOON_BERRIES.get());
        this.tag(ItemTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get().asItem(), ModBlocks.HENBANE.get().asItem(),
                        ModBlocks.WITCHWEED.get().asItem(), ModBlocks.GHOST_FERN.get().asItem(),
                        ModBlocks.NIGHTSHADE_BUSH.get().asItem(), ModBlocks.BEGONIA.get().asItem(),
                        ModBlocks.LAVENDER.get().asItem());
        this.tag(ItemTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.get().asItem(), ModBlocks.HENBANE.get().asItem(),
                        ModBlocks.WITCHWEED.get().asItem(), ModBlocks.GHOST_FERN.get().asItem(),
                        ModBlocks.NIGHTSHADE_BUSH.get().asItem(), ModBlocks.BEGONIA.get().asItem(),
                        ModBlocks.LAVENDER.get().asItem());
        this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(ModItems.RABBAGE_SEEDS.get(),
                ModItems.SUNFIRE_TOMATO_SEEDS.get(), ModItems.MANDRAKE_SEEDS.get());

        // Custom Tags
        this.tag(ModTags.Items.REFINED_HERBS).add(ModItems.DREAM_PASTE.get(),
                ModItems.SPIRIT_POWDER.get(), ModItems.SIREN_PASTE.get(), ModItems.GHOST_POWDER.get());
        this.tag(ModTags.Items.BREWS).add(ModItems.BREW_OF_SIPHON.get(), ModItems.BREW_OF_HOMESTEAD.get(),
                ModItems.BREW_OF_SLIMEWALKER.get(), ModItems.BREW_OF_BLOODLUST.get(),
                ModItems.BREW_OF_SPIKESKIN.get(), ModItems.RUSTIC_BOTTLE.get(),
                ModItems.BREW_OF_DAYBLOOM.get());

        // Forge Tags
        this.tag(ModTags.Items.SALT).add(ModItems.SALT.get());
        this.tag(ModTags.Items.BREAD).add(Items.BREAD);
        this.tag(ModTags.Items.CROPS).add(ModItems.SALTSPROUT.get());
        this.tag(ModTags.Items.CROPS_TOMATO).add(ModItems.SUNFIRE_TOMATO.get());
        this.tag(ModTags.Items.VEGETABLES).add(ModItems.SUNFIRE_TOMATO.get(),
                        Items.CARROT, Items.POTATO);
        this.tag(ModTags.Items.VEGETABLES_TOMATO).add(ModItems.SUNFIRE_TOMATO.get());
        this.tag(ModTags.Items.MUSHROOMS).add(ModBlocks.DREAMSHROOM.get().asItem(),
                ModBlocks.PALE_MUSHROOM.get().asItem());
        this.tag(ModTags.Items.BERRIES).add(ModItems.CHILLBERRIES.get(),
                ModItems.MOON_BERRIES.get());
        this.tag(ModTags.Items.SEEDS).add(ModItems.MANDRAKE_SEEDS.get(),
                ModItems.SUNFIRE_TOMATO_SEEDS.get(), ModItems.RABBAGE_SEEDS.get());
        this.tag(ModTags.Items.SALT_BLOCKS).add(ModBlocks.SALT_BLOCK.get().asItem());
        this.tag(ModTags.Items.COOKED_MEATS).add(Items.COOKED_BEEF, Items.COOKED_CHICKEN,
                Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT,
                Items.COOKED_COD, Items.COOKED_SALMON);

        // Compatibility Tags
        this.tag(ModTags.Compat.SERENE_SEASONS_SPRING_CROPS).add(ModItems.RABBAGE_SEEDS.get())
                .add(ModItems.MANDRAKE_SEEDS.get());
        this.tag(ModTags.Compat.SERENE_SEASONS_SUMMER_CROPS).add(ModItems.RABBAGE_SEEDS.get())
                .add(ModItems.MANDRAKE_SEEDS.get()).add(ModItems.SUNFIRE_TOMATO_SEEDS.get());
        this.tag(ModTags.Compat.SERENE_SEASONS_AUTUMN_CROPS).add(ModItems.RABBAGE_SEEDS.get())
                .add(ModItems.SUNFIRE_TOMATO_SEEDS.get());
        this.tag(ModTags.Compat.SERENE_SEASONS_WINTER_CROPS).add(ModItems.RABBAGE_SEEDS.get());

        // Wood-related Tags
        this.tag(ItemTags.LOGS_THAT_BURN).addTag(ModTags.Items.COTTONWOOD_LOGS)
                .addTag(ModTags.Items.WILLOW_LOGS);
        this.tag(ItemTags.SAPLINGS).add(ModBlocks.COTTONWOOD_SAPLING.get().asItem(),
                ModBlocks.WILLOW_SAPLING.get().asItem());
        this.tag(ItemTags.LEAVES).add(ModBlocks.COTTONWOOD_LEAVES.get().asItem(),
                ModBlocks.WILLOW_LEAVES.get().asItem());
        this.tag(ItemTags.PLANKS).add(ModBlocks.COTTONWOOD_PLANKS.get().asItem(),
                        ModBlocks.WILLOW_PLANKS.get().asItem());
        this.tag(ItemTags.PLANKS).add(ModBlocks.COTTONWOOD_PLANKS.get().asItem(),
                        ModBlocks.WILLOW_PLANKS.get().asItem());
        this.tag(ItemTags.WOODEN_STAIRS).add(ModBlocks.COTTONWOOD_STAIRS.get().asItem(),
                        ModBlocks.WILLOW_STAIRS.get().asItem());
        this.tag(ItemTags.WOODEN_SLABS).add(ModBlocks.COTTONWOOD_SLAB.get().asItem(),
                        ModBlocks.WILLOW_SLAB.get().asItem());
        this.tag(ItemTags.WOODEN_DOORS).add(ModBlocks.COTTONWOOD_DOOR.get().asItem(),
                        ModBlocks.WILLOW_DOOR.get().asItem());
        this.tag(ItemTags.WOODEN_BUTTONS).add(ModBlocks.COTTONWOOD_BUTTON.get().asItem(),
                        ModBlocks.COTTONWOOD_BUTTON.get().asItem());
        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.COTTONWOOD_PRESSURE_PLATE.get().asItem(),
                        ModBlocks.WILLOW_PRESSURE_PLATE.get().asItem());
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(ModBlocks.COTTONWOOD_TRAPDOOR.get().asItem(),
                        ModBlocks.WILLOW_TRAPDOOR.get().asItem());
        this.tag(ItemTags.FENCE_GATES).add(ModBlocks.COTTONWOOD_FENCE_GATE.get().asItem(),
                        ModBlocks.WILLOW_FENCE_GATE.get().asItem());
        this.tag(ItemTags.WOODEN_FENCES).add(ModBlocks.COTTONWOOD_FENCE.get().asItem(),
                        ModBlocks.WILLOW_FENCE.get().asItem());
        this.tag(ModTags.Items.COTTONWOOD_LOGS).add(ModBlocks.COTTONWOOD_LOG.get().asItem(),
                ModBlocks.STRIPPED_COTTONWOOD_LOG.get().asItem(), ModBlocks.COTTONWOOD_WOOD.get().asItem(),
                ModBlocks.STRIPPED_COTTONWOOD_WOOD.get().asItem());
        this.tag(ModTags.Items.WILLOW_LOGS).add(ModBlocks.WILLOW_LOG.get().asItem(),
                ModBlocks.STRIPPED_WILLOW_LOG.get().asItem(), ModBlocks.WILLOW_WOOD.get().asItem(),
                ModBlocks.STRIPPED_WILLOW_WOOD.get().asItem(), ModBlocks.WILLOW_MOSSY_WOOD.get().asItem());
        this.tag(ItemTags.SIGNS).add(ModItems.COTTONWOOD_SIGN.get(), ModItems.WILLOW_SIGN.get());
        this.tag(ItemTags.HANGING_SIGNS).add(ModItems.COTTONWOOD_HANGING_SIGN.get(),
                ModItems.WILLOW_HANGING_SIGN.get());
    }

    @Override
    public String getName() {
        return "Item Tags";
    }
}
