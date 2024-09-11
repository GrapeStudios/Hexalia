package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // Tags
        getOrCreateTagBuilder(ItemTags.FOX_FOOD)
                .add(ModItems.CHILLBERRIES);

        getOrCreateTagBuilder(ItemTags.FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.asItem())
                .add(ModBlocks.HENBANE.asItem())
                .add(ModBlocks.WITCHWEED.asItem());

        getOrCreateTagBuilder(ItemTags.SMALL_FLOWERS)
                .add(ModBlocks.SPIRIT_BLOOM.asItem())
                .add(ModBlocks.HENBANE.asItem())
                .add(ModBlocks.WITCHWEED.asItem());

        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.RABBAGE_SEEDS)
                .add(ModItems.SUNFIRE_TOMATO_SEEDS)
                .add(ModItems.MANDRAKE_SEEDS);

        // Tags for Tree Related Items
        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                .addTag(ModTags.Items.COTTONWOOD_LOGS)
                .addTag(ModTags.Items.WILLOW_LOGS);
        getOrCreateTagBuilder(ItemTags.SAPLINGS)
                .add(ModBlocks.COTTONWOOD_SAPLING.asItem(), ModBlocks.WILLOW_SAPLING.asItem());
        getOrCreateTagBuilder(ItemTags.LEAVES)
                .add(ModBlocks.COTTONWOOD_LEAVES.asItem(), ModBlocks.WILLOW_LEAVES.asItem());

        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.COTTONWOOD_PLANKS.asItem(), 
                        ModBlocks.WILLOW_PLANKS.asItem());
        getOrCreateTagBuilder(ItemTags.PLANKS)
                .add(ModBlocks.COTTONWOOD_PLANKS.asItem(),
                        ModBlocks.WILLOW_PLANKS.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS)
                .add(ModBlocks.COTTONWOOD_STAIRS.asItem(),
                        ModBlocks.WILLOW_STAIRS.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS)
                .add(ModBlocks.COTTONWOOD_SLAB.asItem(),
                        ModBlocks.WILLOW_SLAB.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_DOORS)
                .add(ModBlocks.COTTONWOOD_DOOR.asItem(),
                        ModBlocks.WILLOW_DOOR.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS)
                .add(ModBlocks.COTTONWOOD_BUTTON.asItem(),
                        ModBlocks.COTTONWOOD_BUTTON.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.COTTONWOOD_PRESSURE_PLATE.asItem(),
                        ModBlocks.WILLOW_PRESSURE_PLATE.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.COTTONWOOD_TRAPDOOR.asItem(),
                        ModBlocks.WILLOW_TRAPDOOR.asItem());
        getOrCreateTagBuilder(ItemTags.FENCE_GATES)
                .add(ModBlocks.COTTONWOOD_FENCE_GATE.asItem(),
                        ModBlocks.WILLOW_FENCE_GATE.asItem());
        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES)
                .add(ModBlocks.COTTONWOOD_FENCE.asItem(),
                        ModBlocks.WILLOW_FENCE.asItem());
        getOrCreateTagBuilder(ItemTags.BOATS)
                .add(ModItems.COTTONWOOD_BOAT, ModItems.WILLOW_BOAT);
        getOrCreateTagBuilder(ItemTags.CHEST_BOATS)
                .add(ModItems.COTTONWOOD_CHEST_BOAT, ModItems.WILLOW_CHEST_BOAT);

        getOrCreateTagBuilder(ItemTags.SIGNS)
                .add(ModItems.COTTONWOOD_SIGN, ModItems.WILLOW_SIGN);
        getOrCreateTagBuilder(ItemTags.HANGING_SIGNS)
                .add(ModItems.COTTONWOOD_HANGING_SIGN, ModItems.WILLOW_HANGING_SIGN);

        // Custom Tags
        getOrCreateTagBuilder(ModTags.Items.COOKED_MEATS)
                .add(Items.COOKED_BEEF, Items.COOKED_CHICKEN,
                        Items.COOKED_MUTTON, Items.COOKED_PORKCHOP,
                        Items.COOKED_RABBIT, Items.COOKED_COD,
                        Items.COOKED_SALMON);

        getOrCreateTagBuilder(ModTags.Items.CRUSHED_PLANTS)
                .add(ModItems.DREAMSHROOM_PASTE, ModItems.SIREN_KELP_PASTE,
                        ModItems.SPIRIT_BLOOM_POWDER);

        getOrCreateTagBuilder(ModTags.Items.BREWS)
                .add(ModItems.BREW_OF_HOMESTEAD, ModItems.BREW_OF_VIGOR,
                        ModItems.BREW_OF_SLIMEY_STEP, ModItems.BREW_OF_WARDING,
                        ModItems.BREW_OF_SIPHONING, ModItems.RUSTIC_BOTTLE);

        getOrCreateTagBuilder(ModTags.Items.COTTONWOOD_LOGS)
                .add(ModBlocks.COTTONWOOD_LOG.asItem(), ModBlocks.STRIPPED_COTTONWOOD_LOG.asItem(),
                        ModBlocks.COTTONWOOD_WOOD.asItem(), ModBlocks.STRIPPED_COTTONWOOD_WOOD.asItem());
        getOrCreateTagBuilder(ModTags.Items.WILLOW_LOGS)
                .add(ModBlocks.WILLOW_LOG.asItem(), ModBlocks.STRIPPED_WILLOW_LOG.asItem(),
                        ModBlocks.WILLOW_WOOD.asItem(), ModBlocks.STRIPPED_WILLOW_WOOD.asItem());

        // Common Tags
        getOrCreateTagBuilder(ModTags.Items.SALT_DUSTS)
                .add(ModItems.SALT);

        getOrCreateTagBuilder(ModTags.Items.MUSHROOMS)
                .add(ModBlocks.DREAMSHROOM.asItem())
                .add(ModBlocks.PALE_MUSHROOM.asItem());

        getOrCreateTagBuilder(ModTags.Items.BERRIES)
                .add(ModItems.CHILLBERRIES);

        getOrCreateTagBuilder(ModTags.Items.SEEDS)
                .add(ModItems.MANDRAKE_SEEDS, ModItems.SUNFIRE_TOMATO_SEEDS,
                        ModItems.RABBAGE_SEEDS);

        getOrCreateTagBuilder(ModTags.Items.SALT_ORES)
                .add(ModBlocks.SALT_ORE.asItem());

        getOrCreateTagBuilder(ModTags.Items.SALT_BLOCKS)
                .add(ModBlocks.SALT_BLOCK.asItem());

        getOrCreateTagBuilder(ModTags.Items.ORES)
                .add(ModBlocks.SALT_ORE.asItem());

        // Armor Tags
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.HAT_OF_THE_SWAMP, ModItems.CHEST_OF_THE_SWAMP, ModItems.PANTS_OF_THE_SWAMP, ModItems.BOOTS_OF_THE_SWAMP);
    }
}
