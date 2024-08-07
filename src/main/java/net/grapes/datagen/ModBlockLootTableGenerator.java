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
        addDrop(ModBlocks.SPIRIT_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_SPIRIT_BLOOM);
        addDrop(ModBlocks.DREAMSHROOM);
        addPottedPlantDrops(ModBlocks.POTTED_DREAMSHROOM);
        addDrop(ModBlocks.SIREN_KELP, drops(ModItems.SIREN_KELP));
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
        addDrop(ModBlocks.HENBANE);
        addPottedPlantDrops(ModBlocks.POTTED_HENBANE);
        addDrop(ModBlocks.WILD_SUNFIRE_TOMATO);

        this.addDrop(ModBlocks.WILD_SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS);
        addDrop(ModBlocks.WILD_MANDRAKE);
        this.addDrop(ModBlocks.WILD_MANDRAKE, ModItems.MANDRAKE_SEEDS);
        addDrop(ModBlocks.SALT_BLOCK);
        addDrop(ModBlocks.SALT_ORE, oreDrops(ModBlocks.SALT_ORE, ModItems.SALT));
        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(ModBlocks.MANDRAKE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(MandrakeCropBlock.AGE, 3));
        this.addDrop(ModBlocks.MANDRAKE_CROP, this.cropDrops(ModBlocks.MANDRAKE_CROP, ModItems.MANDRAKE, ModItems.MANDRAKE_SEEDS, builder2));
        BlockStatePropertyLootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(ModBlocks.SUNFIRE_TOMATO_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(SunfireTomatoCropBlock.AGE, 3));
        this.addDrop(ModBlocks.SUNFIRE_TOMATO_CROP, this.cropDrops(ModBlocks.SUNFIRE_TOMATO_CROP, ModItems.SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS, builder3));
        BlockStatePropertyLootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(ModBlocks.RABBAGE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RabbageCropBlock.AGE, 3));
        this.addDrop(ModBlocks.RABBAGE_CROP, this.cropDrops(ModBlocks.RABBAGE_CROP, ModItems.RABBAGE, ModItems.RABBAGE_SEEDS, builder4));

    }
}
