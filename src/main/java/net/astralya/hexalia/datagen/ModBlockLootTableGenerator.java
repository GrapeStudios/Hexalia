package net.astralya.hexalia.datagen;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.*;
import net.astralya.hexalia.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.CaveVines;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.IntProperty;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableGenerator extends FabricBlockLootTableProvider {

    private final RegistryWrapper.WrapperLookup registries;

    public ModBlockLootTableGenerator(FabricDataOutput out, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(out, registries);
        this.registries = registries.join();
    }

    @Override
    public void generate() {
        generatePlantsAndFlowers();
        generateFunctionalBlocks();
        generateCrops();
        generateTreeBlocks();
    }

    private void generatePlantsAndFlowers() {
        // Simple drops
        addDrop(ModBlocks.SPIRIT_BLOOM);
        addDrop(ModBlocks.DREAMSHROOM);
        addDrop(ModBlocks.LOTUS_FLOWER);
        addDrop(ModBlocks.WITCHWEED);
        addDrop(ModBlocks.GHOST_FERN);
        addDrop(ModBlocks.CELESTIAL_BLOOM);
        addDrop(ModBlocks.WITHERED_CELESTIAL_BLOOM);
        addDrop(ModBlocks.NIGHTSHADE_BUSH);
        addDrop(ModBlocks.BEGONIA);
        addDrop(ModBlocks.LAVENDER);
        addDrop(ModBlocks.MORPHORA);
        addDrop(ModBlocks.GRIMSHADE);
        addDrop(ModBlocks.NAUTILITE);
        addDrop(ModBlocks.WINDSONG);
        addDrop(ModBlocks.ASTRYLIS);
        addDrop(ModBlocks.DAHLIA);
        addDrop(ModBlocks.LOURDES);
        addDrop(ModBlocks.AEGIFLORA);
        addDrop(ModBlocks.WITHERED_AEGIFLORA);

        // Potted plants
        addPottedPlantDrops(ModBlocks.POTTED_SPIRIT_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_DREAMSHROOM);
        addPottedPlantDrops(ModBlocks.POTTED_GHOST_FERN);
        addPottedPlantDrops(ModBlocks.POTTED_CELESTIAL_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_WITHERED_CELESTIAL_BLOOM);
        addPottedPlantDrops(ModBlocks.POTTED_NIGHTSHADE_BUSH);
        addPottedPlantDrops(ModBlocks.POTTED_BEGONIA);
        addPottedPlantDrops(ModBlocks.POTTED_LAVENDER);
        addPottedPlantDrops(ModBlocks.POTTED_DAHLIA);
        addPottedPlantDrops(ModBlocks.POTTED_MORPHORA);
        addPottedPlantDrops(ModBlocks.POTTED_GRIMSHADE);
        addPottedPlantDrops(ModBlocks.POTTED_WINDSONG);
        addPottedPlantDrops(ModBlocks.POTTED_ASTRYLIS);
        addPottedPlantDrops(ModBlocks.POTTED_LOURDES);
        addPottedPlantDrops(ModBlocks.POTTED_AEGIFLORA);
        addPottedPlantDrops(ModBlocks.POTTED_WITHERED_AEGIFLORA);

        // Special Plants
        addDrop(ModBlocks.SIREN_KELP, drops(ModItems.SIREN_KELP));
        this.addDrop(ModBlocks.COTTONWOOD_CATKIN, Items.STRING);
        this.addDrop(ModBlocks.GALEBERRIES_VINE, galeberriesDrop(ModBlocks.GALEBERRIES_VINE));
        this.addDrop(ModBlocks.GALEBERRIES_VINE_PLANT, galeberriesDrop(ModBlocks.GALEBERRIES_VINE_PLANT));
    }

    private void generateFunctionalBlocks() {
        // Simple drops
        addDrop(ModBlocks.RITUAL_BRAZIER);
        addDrop(ModBlocks.INFUSED_DIRT);
        addDrop(ModBlocks.SALT_LAMP);
        addDrop(ModBlocks.CANDLE_SKULL);
        addDrop(ModBlocks.WITHER_CANDLE_SKULL);
        addDrop(ModBlocks.DREAMCATCHER);
        addDrop(ModBlocks.RUSTIC_OVEN);
        addDrop(ModBlocks.SMALL_CAULDRON);
        addDrop(ModBlocks.CELESTIAL_CRYSTAL_BLOCK);
        addDrop(ModBlocks.SHELF);
        addDrop(ModBlocks.CENSER);
        addDrop(ModBlocks.MORTAR_AND_PESTLE);
        addDrop(ModBlocks.NESTING_BLOCK);

        // Special drops
        addDrop(ModBlocks.INFUSED_FARMLAND, drops(ModBlocks.INFUSED_DIRT));
        addDrop(ModBlocks.RITUAL_TABLE, drops(ModItems.RITUAL_TABLE));
        this.addDrop(ModBlocks.SALT_BLOCK, block -> oreDrops(ModBlocks.SALT_BLOCK, ModItems.SALT));
        this.addDrop(ModBlocks.SILKWORM_COCOON, ModItems.SILKWORM);
        this.addDrop(ModBlocks.PALE_MUSHROOM, flowerbedDrops(ModBlocks.PALE_MUSHROOM));
        this.addDrop(ModBlocks.EGG_CLUSTER, Items.STRING);
    }

    private void generateCrops() {
        // Wild crops
        this.addDrop(ModBlocks.WILD_SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS);
        this.addDrop(ModBlocks.WILD_MANDRAKE, ModItems.MANDRAKE_SEEDS);

        // Main crops
        var matureMandrake = BlockStatePropertyLootCondition.builder(ModBlocks.MANDRAKE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(MandrakeCropBlock.AGE, 3));
        this.addDrop(ModBlocks.MANDRAKE_CROP,
                this.cropDrops(ModBlocks.MANDRAKE_CROP, ModItems.MANDRAKE, ModItems.MANDRAKE_SEEDS, matureMandrake));

        var matureTomato = BlockStatePropertyLootCondition.builder(ModBlocks.SUNFIRE_TOMATO_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(SunfireTomatoCropBlock.AGE, 3));
        this.addDrop(ModBlocks.SUNFIRE_TOMATO_CROP,
                this.cropDrops(ModBlocks.SUNFIRE_TOMATO_CROP, ModItems.SUNFIRE_TOMATO, ModItems.SUNFIRE_TOMATO_SEEDS, matureTomato));

        var matureRabbage = BlockStatePropertyLootCondition.builder(ModBlocks.RABBAGE_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(RabbageCropBlock.AGE, 3));
        this.addDrop(ModBlocks.RABBAGE_CROP,
                this.cropDrops(ModBlocks.RABBAGE_CROP, ModItems.RABBAGE, ModItems.RABBAGE_SEEDS, matureRabbage));

        // Special crops
        var matureSaltsprout = BlockStatePropertyLootCondition.builder(ModBlocks.SALTSPROUT)
                .properties(StatePredicate.Builder.create().exactMatch(SaltsproutBlock.AGE, 2));
        this.addDrop(ModBlocks.SALTSPROUT,
                this.createSimpleCropBlock(ModBlocks.SALTSPROUT, ModItems.SALTSPROUT, matureSaltsprout));

        this.addDrop(ModBlocks.CHILLBERRY_BUSH,
                createHarvestablePlantBlock(ModBlocks.CHILLBERRY_BUSH, ModItems.CHILLBERRIES,
                        ChillberryBushBlock.AGE, 3, 2.0F, 3.0F));
    }

    private void generateTreeBlocks() {
        // Cottonwood
        addDrop(ModBlocks.COTTONWOOD_LEAVES,
                leavesDrops(ModBlocks.COTTONWOOD_LEAVES, ModBlocks.COTTONWOOD_SAPLING, SAPLING_DROP_CHANCE));
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
        // signs
         addDrop(ModBlocks.COTTONWOOD_SIGN, ModBlocks.COTTONWOOD_SIGN.asItem());
         addDrop(ModBlocks.COTTONWOOD_WALL_SIGN, ModBlocks.COTTONWOOD_SIGN.asItem());
         addDrop(ModBlocks.COTTONWOOD_HANGING_SIGN, ModBlocks.COTTONWOOD_HANGING_SIGN.asItem());
         addDrop(ModBlocks.COTTONWOOD_HANGING_WALL_SIGN, ModBlocks.COTTONWOOD_HANGING_SIGN.asItem());

        // Willow
        addDrop(ModBlocks.WILLOW_LEAVES,
                leavesDrops(ModBlocks.WILLOW_LEAVES, ModBlocks.WILLOW_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(ModBlocks.WILLOW_LOG);
        addDrop(ModBlocks.WILLOW_WOOD);
        addDrop(ModBlocks.STRIPPED_WILLOW_LOG);
        addDrop(ModBlocks.STRIPPED_WILLOW_WOOD);
        addDrop(ModBlocks.WILLOW_PLANKS);
        addDrop(ModBlocks.WILLOW_SAPLING);
        addPottedPlantDrops(ModBlocks.POTTED_WILLOW_SAPLING);
        addDrop(ModBlocks.WILLOW_STAIRS);
        addDrop(ModBlocks.WILLOW_PRESSURE_PLATE);
        addDrop(ModBlocks.WILLOW_SLAB, slabDrops(ModBlocks.WILLOW_SLAB));
        addDrop(ModBlocks.WILLOW_BUTTON);
        addDrop(ModBlocks.WILLOW_FENCE);
        addDrop(ModBlocks.WILLOW_FENCE_GATE);
        addDrop(ModBlocks.WILLOW_TRAPDOOR);
        addDrop(ModBlocks.WILLOW_DOOR, doorDrops(ModBlocks.WILLOW_DOOR));
        // signs
         addDrop(ModBlocks.WILLOW_SIGN, ModBlocks.WILLOW_SIGN.asItem());
         addDrop(ModBlocks.WILLOW_WALL_SIGN, ModBlocks.WILLOW_SIGN.asItem());
         addDrop(ModBlocks.WILLOW_HANGING_SIGN, ModBlocks.WILLOW_HANGING_SIGN.asItem());
         addDrop(ModBlocks.WILLOW_HANGING_WALL_SIGN, ModBlocks.WILLOW_HANGING_SIGN.asItem());
    }

    protected LootTable.Builder galeberriesDrop(Block drop) {
        return LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.GALEBERRIES))
                        .conditionally(BlockStatePropertyLootCondition.builder(drop)
                                .properties(StatePredicate.Builder.create()
                                        .exactMatch(CaveVines.BERRIES, true))));
    }

    protected LootTable.Builder createSimpleCropBlock(Block cropBlock,
                                                      Item grownItem,
                                                      LootCondition.Builder whenMature) {
        RegistryEntry<Enchantment> fortune = this.registries
                .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);

        LootTable.Builder table = LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(grownItem)
                                .conditionally(whenMature)
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(fortune, 0.5714286F, 3))));

        return this.applyExplosionDecay(cropBlock, table);
    }

    protected LootTable.Builder createHarvestablePlantBlock(Block bushBlock,
                                                            Item harvestedItem,
                                                            IntProperty ageProperty,
                                                            int maxAge,
                                                            float minDropAtMax,
                                                            float maxDropAtMax) {
        RegistryEntry<Enchantment> fortune = this.registries
                .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);

        LootCondition.Builder maxAgeCondition = BlockStatePropertyLootCondition.builder(bushBlock)
                .properties(StatePredicate.Builder.create().exactMatch(ageProperty, maxAge));

        return LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(harvestedItem)))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(harvestedItem)
                                .conditionally(maxAgeCondition)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(minDropAtMax, maxDropAtMax)))
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(fortune, 1, 2))));
    }
}