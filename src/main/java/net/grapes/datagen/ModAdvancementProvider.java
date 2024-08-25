package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.LocationPredicate;
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
                .display(new AdvancementDisplay(new ItemStack(ModItems.HEXBOOK),
                        Text.translatable("advancements.hexalia.root.title"),
                        Text.translatable("advancements.hexalia.root.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_hexbook", InventoryChangedCriterion.Conditions.items(ModItems.HEXBOOK))
                .build(consumer, HexaliaMod.MOD_ID + ":root");

        Advancement smallCauldron = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.SMALL_CAULDRON),
                        Text.translatable("advancements.hexalia.small_cauldron.title"),
                        Text.translatable("advancements.hexalia.small_cauldron.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_small_cauldron", InventoryChangedCriterion.Conditions.items(ModItems.SMALL_CAULDRON))
                .parent(rootAdvancement).build(consumer, HexaliaMod.MOD_ID + ":small_cauldron");

        Advancement mortarandPestle = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.MORTAR_AND_PESTLE),
                        Text.translatable("advancements.hexalia.mortar_and_pestle.title"),
                        Text.translatable("advancements.hexalia.mortar_and_pestle.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_mortar_and_pestle", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_AND_PESTLE))
                .parent(rootAdvancement).build(consumer, HexaliaMod.MOD_ID + "mortar_and_pestle");

        Advancement stoneDagger = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.STONE_DAGGER),
                        Text.translatable("advancements.hexalia.stone_dagger.title"),
                        Text.translatable("advancements.hexalia.stone_dagger.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_stone_dagger", InventoryChangedCriterion.Conditions.items(ModItems.STONE_DAGGER))
                .parent(rootAdvancement).build(consumer, HexaliaMod.MOD_ID + "stone_dagger");

        Advancement slimeyBrew = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.BREW_OF_SLIMEY_STEP),
                        Text.translatable("advancements.hexalia.slimey_brew.title"),
                        Text.translatable("advancements.hexalia.slimey_brew.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.GOAL,
                        true, true, false))
                .criterion("has_slimey_step_brew", InventoryChangedCriterion.Conditions.items(ModItems.BREW_OF_SLIMEY_STEP))
                .parent(smallCauldron).build(consumer, HexaliaMod.MOD_ID + "slimey_brew");

        Advancement saltItem = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.SALT),
                        Text.translatable("advancements.hexalia.salt_item.title"),
                        Text.translatable("advancements.hexalia.salt_item.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_salt", InventoryChangedCriterion.Conditions.items(ModItems.SALT))
                .parent(rootAdvancement).build(consumer, HexaliaMod.MOD_ID + "salt");

        Advancement purifyingSalts = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.PURIFYING_SALTS),
                        Text.translatable("advancements.hexalia.purifying_salts.title"),
                        Text.translatable("advancements.hexalia.purifying_salts.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.GOAL,
                        true, true, false))
                .criterion("has_purifying_salts", InventoryChangedCriterion.Conditions.items(ModItems.PURIFYING_SALTS))
                .parent(saltItem).build(consumer, HexaliaMod.MOD_ID + "purifying_salts");

        Advancement ritualTable = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.RITUAL_TABLE),
                        Text.translatable("advancements.hexalia.ritual_table.title"),
                        Text.translatable("advancements.hexalia.ritual_table.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_ritual_table", InventoryChangedCriterion.Conditions.items(ModItems.RITUAL_TABLE))
                .parent(saltItem).build(consumer, HexaliaMod.MOD_ID + "ritual_table");

        Advancement hexFocus = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.HEX_FOCUS),
                        Text.translatable("advancements.hexalia.hex_focus.title"),
                        Text.translatable("advancements.hexalia.hex_focus.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_hex_focus", InventoryChangedCriterion.Conditions.items(ModItems.HEX_FOCUS))
                .parent(ritualTable).build(consumer, HexaliaMod.MOD_ID + "hex_focus");

        Advancement rabbage = Advancement.Builder.create()
                .display(new AdvancementDisplay(new ItemStack(ModItems.RABBAGE),
                        Text.translatable("advancements.hexalia.rabbage.title"),
                        Text.translatable("advancements.hexalia.rabbage.description"),
                        new Identifier(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), AdvancementFrame.TASK,
                        true, true, false))
                .criterion("has_rabbage", InventoryChangedCriterion.Conditions.items(ModItems.RABBAGE))
                .parent(ritualTable).build(consumer, HexaliaMod.MOD_ID + "rabbage");
    }
}
