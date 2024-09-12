package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.MandrakeCropBlock;
import net.grapes.hexalia.block.custom.RabbageCropBlock;
import net.grapes.hexalia.block.custom.SunfireTomatoCropBlock;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {

    public ModBlockLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {

        // Plants & Flowers
        addDrop(ModBlocks.SPIRIT_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_SPIRIT_BLOOM);
        addDrop(ModBlocks.DREAMSHROOM);
        addPottedPlantDrops(ModBlocks.POTTED_DREAMSHROOM);
        addDrop(ModBlocks.SIREN_KELP, drops(ModItems.SIREN_KELP));
        addDrop(ModBlocks.HENBANE);
        addPottedPlantDrops(ModBlocks.POTTED_HENBANE);
        addDrop(ModBlocks.LOTUS_FLOWER);
        addDrop(ModBlocks.PALE_MUSHROOM);
        addPottedPlantDrops(ModBlocks.POTTED_PALE_MUSHROOM);
        addDrop(ModBlocks.WITCHWEED);
        addDrop(ModBlocks.GHOST_FERN);
        addDrop(ModBlocks.HEXED_BULRUSH);

        // Other Blocks
        addDrop(ModBlocks.INFUSED_DIRT);
        addDrop(ModBlocks.INFUSED_FARMLAND, drops(ModBlocks.INFUSED_DIRT));
        addDrop(ModBlocks.RITUAL_TABLE, drops(ModItems.RITUAL_TABLE));
        addDrop(ModBlocks.SALT);
        addDrop(ModBlocks.SALT_LAMP);
        addDrop(ModBlocks.CANDLE_SKULL);
        addDrop(ModBlocks.DREAMCATCHER);
        addDrop(ModBlocks.PARCHMENT);
        addDrop(ModBlocks.BREW_SHELF);
        addDrop(ModBlocks.RUSTIC_OVEN);
        addDrop(ModBlocks.SMALL_CAULDRON);
        addDrop(ModBlocks.SALT_BLOCK);
        addDrop(ModBlocks.SALT_ORE, oreDrops(ModBlocks.SALT_ORE, ModItems.SALT));
        addDrop(ModBlocks.SILKWORM_COCOON);
        this.addDrop(ModBlocks.SILKWORM_COCOON, ModItems.SILKWORM);

        // Crop Blocks
        addDrop(ModBlocks.WILD_SUNFIRE_TOMATO);
        this.addDrop(ModBlocks.WILD_SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS);
        addDrop(ModBlocks.WILD_MANDRAKE);
        this.addDrop(ModBlocks.WILD_MANDRAKE, ModItems.MANDRAKE_SEEDS);
        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(ModBlocks.MANDRAKE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(MandrakeCropBlock.AGE, 3));
        this.addDrop(ModBlocks.MANDRAKE_CROP, this.cropDrops(ModBlocks.MANDRAKE_CROP, ModItems.MANDRAKE, ModItems.MANDRAKE_SEEDS, builder2));
        BlockStatePropertyLootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(ModBlocks.SUNFIRE_TOMATO_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(SunfireTomatoCropBlock.AGE, 3));
        this.addDrop(ModBlocks.SUNFIRE_TOMATO_CROP, this.cropDrops(ModBlocks.SUNFIRE_TOMATO_CROP, ModItems.SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS, builder3));
        BlockStatePropertyLootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(ModBlocks.RABBAGE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RabbageCropBlock.AGE, 3));
        this.addDrop(ModBlocks.RABBAGE_CROP, this.cropDrops(ModBlocks.RABBAGE_CROP, ModItems.RABBAGE, ModItems.RABBAGE_SEEDS, builder4));

        // Drops for Tree-Related Blocks
        addDrop(ModBlocks.COTTONWOOD_LEAVES, leavesDrops(ModBlocks.COTTONWOOD_LEAVES, ModBlocks.COTTONWOOD_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(ModBlocks.COTTONWOOD_LOG);
        addDrop(ModBlocks.COTTONWOOD_WOOD);
        addDrop(ModBlocks.STRIPPED_COTTONWOOD_LOG);
        addDrop(ModBlocks.STRIPPED_COTTONWOOD_WOOD);
        addDrop(ModBlocks.COTTONWOOD_PLANKS);
        addDrop(ModBlocks.COTTONWOOD_SAPLING);
        addPottedPlantDrops(ModBlocks.POTTED_COTTONWOOD_SAPLING);
        addDrop(ModBlocks.COTTONWOOD_STAIRS);
        addDrop(ModBlocks.COTTONWOOD_PRESSURE_PLATE);
        addDrop(ModBlocks.COTTONWOOD_SLAB, slabDrops(ModBlocks.COTTONWOOD_SLAB));
        addDrop(ModBlocks.COTTONWOOD_BUTTON);
        addDrop(ModBlocks.COTTONWOOD_FENCE);
        addDrop(ModBlocks.COTTONWOOD_FENCE_GATE);
        addDrop(ModBlocks.COTTONWOOD_TRAPDOOR);
        addDrop(ModBlocks.COTTONWOOD_DOOR, doorDrops(ModBlocks.COTTONWOOD_DOOR));
        addDrop(ModBlocks.COTTONWOOD_SIGN);
        addDrop(ModBlocks.COTTONWOOD_WALL_SIGN);
        addDrop(ModBlocks.COTTONWOOD_HANGING_WALL_SIGN);
        addDrop(ModBlocks.COTTONWOOD_HANGING_SIGN);

        addDrop(ModBlocks.WILLOW_LEAVES, leavesDrops(ModBlocks.WILLOW_LEAVES, ModBlocks.WILLOW_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(ModBlocks.WILLOW_LOG);
        addDrop(ModBlocks.WILLOW_WOOD);
        addDrop(ModBlocks.STRIPPED_WILLOW_LOG);
        addDrop(ModBlocks.STRIPPED_WILLOW_WOOD);
        addDrop(ModBlocks.WILLOW_SAPLING);
        addPottedPlantDrops(ModBlocks.POTTED_WILLOW_SAPLING);
        addDrop(ModBlocks.WILLOW_PLANKS);
        addDrop(ModBlocks.WILLOW_STAIRS);
        addDrop(ModBlocks.WILLOW_PRESSURE_PLATE);
        addDrop(ModBlocks.WILLOW_SLAB, slabDrops(ModBlocks.WILLOW_SLAB));
        addDrop(ModBlocks.WILLOW_BUTTON);
        addDrop(ModBlocks.WILLOW_FENCE);
        addDrop(ModBlocks.WILLOW_FENCE_GATE);
        addDrop(ModBlocks.WILLOW_TRAPDOOR);
        addDrop(ModBlocks.WILLOW_DOOR, doorDrops(ModBlocks.WILLOW_DOOR));
        addDrop(ModBlocks.WILLOW_SIGN);
        addDrop(ModBlocks.WILLOW_WALL_SIGN);
        addDrop(ModBlocks.WILLOW_HANGING_WALL_SIGN);
        addDrop(ModBlocks.WILLOW_HANGING_SIGN);
    }
}
