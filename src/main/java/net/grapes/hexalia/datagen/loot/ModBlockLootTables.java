package net.grapes.hexalia.datagen.loot;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.block.custom.*;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        // Plants & Flowers
        this.dropSelf(ModBlocks.SPIRIT_BLOOM.get());
        this.add(ModBlocks.POTTED_SPIRIT_BLOOM.get(),
                createPotFlowerItemTable(ModBlocks.POTTED_SPIRIT_BLOOM.get()));
        this.dropSelf(ModBlocks.DREAMSHROOM.get());
        this.add(ModBlocks.POTTED_DREAMSHROOM.get(),
                createPotFlowerItemTable(ModBlocks.POTTED_DREAMSHROOM.get()));
        this.add(ModBlocks.SIREN_KELP.get(),
                this.createSingleItemTable(ModItems.SIREN_KELP.get()));
        this.dropSelf(ModBlocks.HENBANE.get());
        this.add(ModBlocks.POTTED_HENBANE.get(),
                createPotFlowerItemTable(ModBlocks.POTTED_HENBANE.get()));
        this.dropSelf(ModBlocks.LOTUS_FLOWER.get());
        this.dropSelf(ModBlocks.PALE_MUSHROOM.get());
        this.add(ModBlocks.POTTED_PALE_MUSHROOM.get(),
                createPotFlowerItemTable(ModBlocks.POTTED_PALE_MUSHROOM.get()));
        this.dropSelf(ModBlocks.WITCHWEED.get());
        this.dropSelf(ModBlocks.GHOST_FERN.get());
        this.dropSelf(ModBlocks.NIGHTSHADE_BUSH.get());
        this.dropSelf(ModBlocks.DUCKWEED.get());
        this.add(ModBlocks.POTTED_NIGHTSHADE_BUSH.get(),
                createPotFlowerItemTable(ModBlocks.POTTED_NIGHTSHADE_BUSH.get()));
        this.add(ModBlocks.HEXED_BULRUSH.get(),
                createTallPlantBlock(ModBlocks.HEXED_BULRUSH.get()));
        this.add(ModBlocks.COTTONWOOD_CATKIN.get(),
                this.createSingleItemTable(Items.STRING));

        // General Blocks
        this.dropSelf(ModBlocks.INFUSED_DIRT.get());
        this.add(ModBlocks.INFUSED_FARMLAND.get(),
                this.createSingleItemTable(ModBlocks.INFUSED_DIRT.get()));
        this.add(ModBlocks.RITUAL_TABLE.get(),
                this.createSingleItemTable(ModItems.RITUAL_TABLE.get()));
        this.dropSelf(ModBlocks.SALT.get());
        this.dropSelf(ModBlocks.SALT_LAMP.get());
        this.dropSelf(ModBlocks.CANDLE_SKULL.get());
        this.dropSelf(ModBlocks.DREAMCATCHER.get());
        this.dropSelf(ModBlocks.PARCHMENT.get());
        this.dropSelf(ModBlocks.BREW_SHELF.get());
        this.dropSelf(ModBlocks.RUSTIC_OVEN.get());
        this.dropSelf(ModBlocks.SMALL_CAULDRON.get());
        this.add(ModBlocks.SALT_ORE.get(), block ->
                createOreDrop(ModBlocks.SALT_ORE.get(), ModItems.SALT.get()));
        this.add(ModBlocks.SALT_BLOCK.get(), block ->
                createOreDrop(ModBlocks.SALT_ORE.get(), ModItems.SALT.get()));
        this.add(ModBlocks.SILKWORM_COCOON.get(),
                this.createSingleItemTable(ModItems.SILKWORM.get()));

        // Crop Blocks
        this.add(ModBlocks.WILD_SUNFIRE_TOMATO.get(),
                this.createSingleItemTable(ModItems.SUNFIRE_TOMATO_SEEDS.get()));
        this.add(ModBlocks.WILD_MANDRAKE.get(),
                this.createSingleItemTable(ModItems.MANDRAKE_SEEDS.get()));
        LootItemCondition.Builder lootItemCondition$builder1 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.MANDRAKE_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MandrakeCropBlock.AGE, 3));
        this.add(ModBlocks.MANDRAKE_CROP.get(), this.createCropDrops(ModBlocks.MANDRAKE_CROP.get(),
                ModItems.MANDRAKE.get(), ModItems.MANDRAKE_SEEDS.get(), lootItemCondition$builder1));
        LootItemCondition.Builder lootItemCondition$builder2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SUNFIRE_TOMATO_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SunfireTomatoCropBlock.AGE, 3));
        this.add(ModBlocks.SUNFIRE_TOMATO_CROP.get(), this.createCropDrops(ModBlocks.SUNFIRE_TOMATO_CROP.get(),
                ModItems.SUNFIRE_TOMATO.get(), ModItems.SUNFIRE_TOMATO_SEEDS.get(), lootItemCondition$builder2));
        LootItemCondition.Builder lootItemCondition$builder3 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.RABBAGE_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RabbageCropBlock.AGE, 3));
        this.add(ModBlocks.RABBAGE_CROP.get(), this.createCropDrops(ModBlocks.RABBAGE_CROP.get(),
                ModItems.RABBAGE.get(), ModItems.RABBAGE_SEEDS.get(), lootItemCondition$builder3));
        LootItemCondition.Builder lootItemCondition$builder4 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SALTSPROUT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SaltsproutBlock.AGE, 2));
        this.add(ModBlocks.SALTSPROUT.get(), this.createSimpleCropBlock(ModBlocks.SALTSPROUT.get(),
                ModItems.SALTSPROUT.get(), lootItemCondition$builder4));
        this.add(ModBlocks.CHILLBERRY_BUSH.get(), createHarvestablePlantBlock(ModBlocks.CHILLBERRY_BUSH.get(), ModItems.CHILLBERRIES.get()));
    }

    protected LootTable.Builder createSimpleCropBlock(Block pCropBlock, Item pGrownCropItem, LootItemCondition.Builder pDropGrownCropCondition) {
        return this.applyExplosionDecay(pCropBlock, LootTable.lootTable().withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(pGrownCropItem).when(pDropGrownCropCondition)
                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
    }

    protected LootTable.Builder createHarvestablePlantBlock(Block bushBlock, Item chillberryItem) {
        LootItemCondition.Builder age3Condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(bushBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ChillberryBushBlock.AGE, 3));
        LootItemCondition.Builder age2Condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(bushBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ChillberryBushBlock.AGE, 2));
        return this.applyExplosionDecay(bushBlock, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(chillberryItem)
                                .when(age3Condition)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 1,3))))
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(chillberryItem)
                                .when(age2Condition)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 1, 3))))
        );
    }

    protected LootTable.Builder createTallPlantBlock(Block bulrushBlock) {
        LootItemCondition.Builder lowerHalfCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(bulrushBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HexedBulrushBlock.HALF, DoubleBlockHalf.LOWER));

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(bulrushBlock)
                                .when(lowerHalfCondition)));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
