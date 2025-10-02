package net.astralya.hexalia.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.*;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.CaveVines;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.property.IntProperty;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {

    public ModBlockLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        generatePlantsAndFlowers();
        generateFunctionalBlocks();
        generateCrops();
        generateTreeBlocks();
    }

    private void generatePlantsAndFlowers() {
        addDrop(ModBlocks.SPIRIT_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_SPIRIT_BLOOM);
        addDrop(ModBlocks.DREAMSHROOM);
        addPottedPlantDrops(ModBlocks.POTTED_DREAMSHROOM);
        addDrop(ModBlocks.SIREN_KELP, drops(ModItems.SIREN_KELP));
        addDrop(ModBlocks.LOTUS_FLOWER);
        addDrop(ModBlocks.PALE_MUSHROOM);
        addDrop(ModBlocks.WITCHWEED);
        addDrop(ModBlocks.GHOST_FERN);
        addPottedPlantDrops(ModBlocks.POTTED_GHOST_FERN);
        addDrop(ModBlocks.NIGHTSHADE_BUSH);
        addPottedPlantDrops(ModBlocks.POTTED_NIGHTSHADE_BUSH);
        this.addDrop(ModBlocks.COTTONWOOD_CATKIN, Items.STRING);
        addDrop(ModBlocks.BEGONIA);
        addPottedPlantDrops(ModBlocks.POTTED_BEGONIA);
        addDrop(ModBlocks.LAVENDER);
        addPottedPlantDrops(ModBlocks.POTTED_LAVENDER);
        addDrop(ModBlocks.MORPHORA);
        addPottedPlantDrops(ModBlocks.POTTED_MORPHORA);
        addDrop(ModBlocks.GRIMSHADE);
        addPottedPlantDrops(ModBlocks.POTTED_GRIMSHADE);
        addDrop(ModBlocks.NAUTILITE);
        addDrop(ModBlocks.WINDSONG);
        addPottedPlantDrops(ModBlocks.POTTED_WINDSONG);
        addDrop(ModBlocks.ASTRYLIS);
        addPottedPlantDrops(ModBlocks.POTTED_ASTRYLIS);
        this.addDrop(ModBlocks.GALEBERRIES_VINE, galeberriesDrop(ModBlocks.GALEBERRIES_VINE));
        this.addDrop(ModBlocks.GALEBERRIES_VINE_PLANT, galeberriesDrop(ModBlocks.GALEBERRIES_VINE_PLANT));
        addDrop(ModBlocks.DAHLIA);
        addPottedPlantDrops(ModBlocks.POTTED_DAHLIA);
        addDrop(ModBlocks.CELESTIAL_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_CELESTIAL_BLOOM);
    }

    private void generateFunctionalBlocks() {
        addDrop(ModBlocks.RITUAL_BRAZIER);
        addDrop(ModBlocks.CELESTIAL_CRYSTAL_BLOCK);
        addDrop(ModBlocks.INFUSED_DIRT);
        addDrop(ModBlocks.INFUSED_FARMLAND, drops(ModBlocks.INFUSED_DIRT));
        addDrop(ModBlocks.RITUAL_TABLE, drops(ModItems.RITUAL_TABLE));
        addDrop(ModBlocks.SALT_LAMP);
        addDrop(ModBlocks.CANDLE_SKULL);
        addDrop(ModBlocks.DREAMCATCHER);
        addDrop(ModBlocks.RUSTIC_OVEN);
        addDrop(ModBlocks.SMALL_CAULDRON);
        addDrop(ModBlocks.SILKWORM_COCOON);
        this.addDrop(ModBlocks.SILKWORM_COCOON, ModItems.SILKWORM);
        addDrop(ModBlocks.SALT_BLOCK, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.SALT)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 3.0f)))
                        )
                )
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.SALT)
                                .conditionally(WITH_SILK_TOUCH.invert())
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)))
                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                        )
                )
        );
        addDrop(ModBlocks.SHELF);
        addDrop(ModBlocks.CENSER);
    }

    private void generateCrops() {
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
        BlockStatePropertyLootCondition.Builder builder5 = BlockStatePropertyLootCondition.builder(ModBlocks.SALTSPROUT)
                .properties(StatePredicate.Builder.create().exactMatch(RabbageCropBlock.AGE, 3));
        this.addDrop(ModBlocks.SALTSPROUT, this.createSimpleCropBlock(ModBlocks.SALTSPROUT, ModItems.SALTSPROUT, builder5));
        this.addDrop(ModBlocks.CHILLBERRY_BUSH, createHarvestablePlantBlock(
                ModBlocks.CHILLBERRY_BUSH, ModItems.CHILLBERRIES,
                ChillberryBushBlock.AGE, 3, 2.0F, 3.0F));
    }

    private void generateTreeBlocks() {
        // Cottonwood
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
    }

    protected LootTable.Builder galeberriesDrop(Block drop) {
        return LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(ModItems.GALEBERRIES))
                .conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create()
                        .exactMatch(CaveVines.BERRIES, true))));
    }

    protected LootTable.Builder createSimpleCropBlock(Block cropBlock, Item cropItem, LootCondition.Builder dropGrownCropCondition) {
        return LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(cropItem))
                )
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(cropItem)
                                .conditionally(dropGrownCropCondition)
                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                        )
                )
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(cropItem)
                                .conditionally(dropGrownCropCondition)
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))
                        )
                );
    }

    protected LootTable.Builder createHarvestablePlantBlock(Block bushBlock, Item harvestedItem, IntProperty ageProperty, int maxAge, float minDropAtMaxAge, float maxDropAtMaxAge) {
        LootCondition.Builder maxAgeCondition = BlockStatePropertyLootCondition.builder(bushBlock)
                .properties(StatePredicate.Builder.create().exactMatch(ageProperty, maxAge));

        return LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(harvestedItem))
                )
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(harvestedItem)
                                .conditionally(maxAgeCondition)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDropAtMaxAge, maxDropAtMaxAge)))
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 1, 2))));
    }
}
