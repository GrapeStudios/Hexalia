package net.astralya.hexalia.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    public ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement rootAdvancement = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.HEX_FOCUS),
                        Text.translatable("advancements.hexalia.root.title"),
                        Text.translatable("advancements.hexalia.root.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"),
                        AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_hex_focus", InventoryChangedCriterion.Conditions.items(ModItems.HEX_FOCUS))
                .build(consumer, HexaliaMod.MODID + ":root");

        Advancement smallCauldron = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.SMALL_CAULDRON),
                        Text.translatable("advancements.hexalia.small_cauldron.title"),
                        Text.translatable("advancements.hexalia.small_cauldron.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"),
                        AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("has_small_cauldron", InventoryChangedCriterion.Conditions.items(ModItems.SMALL_CAULDRON))
                .build(consumer, HexaliaMod.MODID + ":small_cauldron");

        Advancement mortarAndPestle = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.MORTAR_AND_PESTLE),
                        Text.translatable("advancements.hexalia.mortar_and_pestle.title"),
                        Text.translatable("advancements.hexalia.mortar_and_pestle.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("has_mortar_and_pestle", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_AND_PESTLE))
                .build(consumer, HexaliaMod.MODID + ":mortar_and_pestle");

        Advancement stoneDagger = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.ATHAME),
                        Text.translatable("advancements.hexalia.stone_dagger.title"),
                        Text.translatable("advancements.hexalia.stone_dagger.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("has_stone_dagger", InventoryChangedCriterion.Conditions.items(ModItems.ATHAME))
                .build(consumer, HexaliaMod.MODID + ":stone_dagger");

        Advancement slimeyBrew = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.BREW_OF_SLIMEWALKER),
                        Text.translatable("advancements.hexalia.slimey_brew.title"),
                        Text.translatable("advancements.hexalia.slimey_brew.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.GOAL,
                        true, true, false))
                .parent(smallCauldron)
                .criterion("use_slimewalker_brew", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_DAYBLOOM))
                .build(consumer, HexaliaMod.MODID + ":slimey_brew");

        Advancement saltItem = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.SALT),
                        Text.translatable("advancements.hexalia.salt_item.title"),
                        Text.translatable("advancements.hexalia.salt_item.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("has_salt", InventoryChangedCriterion.Conditions.items(ModItems.SALT))
                .build(consumer, HexaliaMod.MODID + ":salt");

        Advancement purifyingSalts = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.PURIFYING_SAC),
                        Text.translatable("advancements.hexalia.purifying_salts.title"),
                        Text.translatable("advancements.hexalia.purifying_salts.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.GOAL,
                        true, true, false))
                .parent(saltItem)
                .criterion("has_purifying_salts", InventoryChangedCriterion.Conditions.items(ModItems.PURIFYING_SAC))
                .build(consumer, HexaliaMod.MODID + ":purifying_salts");

        Advancement ritualTable = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.RITUAL_TABLE),
                        Text.translatable("advancements.hexalia.ritual_table.title"),
                        Text.translatable("advancements.hexalia.ritual_table.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("has_ritual_table", InventoryChangedCriterion.Conditions.items(ModItems.RITUAL_TABLE))
                .build(consumer, HexaliaMod.MODID + ":ritual_table");

        Advancement rabbage = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.RABBAGE),
                        Text.translatable("advancements.hexalia.rabbage.title"),
                        Text.translatable("advancements.hexalia.rabbage.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(ritualTable)
                .criterion("has_rabbage", InventoryChangedCriterion.Conditions.items(ModItems.RABBAGE))
                .build(consumer, HexaliaMod.MODID + ":rabbage");

        Advancement weatherIdols = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.TEMPEST_IDOL),
                        Text.translatable("advancements.hexalia.weather_idol.title"),
                        Text.translatable("advancements.hexalia.weather_idol.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .criterion("use_rain_idol", ConsumeItemCriterion.Conditions.item(ModItems.RAINFALL_IDOL))
                .criterion("has_clear_idol", ConsumeItemCriterion.Conditions.item(ModItems.CLARITY_IDOL))
                .criterion("has_storm_idol", ConsumeItemCriterion.Conditions.item(ModItems.TEMPEST_IDOL))
                .build(consumer, HexaliaMod.MODID + ":elemental_nodes");

        Advancement elementalNodes = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.FIRE_NODE),
                        Text.translatable("advancements.hexalia.elemental_nodes.title"),
                        Text.translatable("advancements.hexalia.elemental_nodes.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(ritualTable)
                .criterion("has_water_node", InventoryChangedCriterion.Conditions.items(ModItems.WATER_NODE))
                .criterion("has_air_node", InventoryChangedCriterion.Conditions.items(ModItems.AIR_NODE))
                .criterion("has_earth_node", InventoryChangedCriterion.Conditions.items(ModItems.EARTH_NODE))
                .criterion("has_fire_node", InventoryChangedCriterion.Conditions.items(ModItems.FIRE_NODE))
                .build(consumer, HexaliaMod.MODID + ":elemental_nodes");

        Advancement ritualBrazier = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModBlocks.RITUAL_BRAZIER),
                        Text.translatable("advancements.hexalia.ritual_brazier.title"),
                        Text.translatable("advancements.hexalia.ritual_brazier.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(ritualTable)
                .criterion("has_ritual_brazier", InventoryChangedCriterion.Conditions.items(ModBlocks.RITUAL_BRAZIER))
                .build(consumer, HexaliaMod.MODID + ":ritual_brazier");

        Advancement celestialCrystal = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.CELESTIAL_CRYSTAL),
                        Text.translatable("advancements.hexalia.celestial_crystal.title"),
                        Text.translatable("advancements.hexalia.celestial_crystal.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"), AdvancementFrame.TASK,
                        true, true, false))
                .parent(ritualBrazier)
                .criterion("has_celestial_crystal", InventoryChangedCriterion.Conditions.items(ModItems.CELESTIAL_CRYSTAL))
                .build(consumer, HexaliaMod.MODID + ":celestial_crystal");

        Advancement daybloomBrew = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.BREW_OF_DAYBLOOM),
                        Text.translatable("advancements.hexalia.daybloom_brew.title"),
                        Text.translatable("advancements.hexalia.daybloom_brew.description"),
                        new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png"),
                        AdvancementFrame.TASK,
                        true, true, false))
                .parent(smallCauldron)
                .criterion("use_daybloom", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_DAYBLOOM))
                .build(consumer, HexaliaMod.MODID + ":daybloom_brew");
    }
}