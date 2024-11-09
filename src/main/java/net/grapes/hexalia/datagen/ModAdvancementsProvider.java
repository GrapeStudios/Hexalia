package net.grapes.hexalia.datagen;

import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class ModAdvancementsProvider implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {

        Advancement rootAdvancement = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.HEXBOOK.get()),
                        Component.translatable("advancements.hexalia.root.title"),
                        Component.translatable("advancements.hexalia.root.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .addCriterion("has_hexbook", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.HEXBOOK.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "root"), existingFileHelper);

        Advancement smallCauldron = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.SMALL_CAULDRON.get()),
                        Component.translatable("advancements.hexalia.small_cauldron.title"),
                        Component.translatable("advancements.hexalia.small_cauldron.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .addCriterion("has_small_cauldron", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SMALL_CAULDRON.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "small_cauldron"), existingFileHelper);

        Advancement mortarAndPestle = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.MORTAR_AND_PESTLE.get()),
                        Component.translatable("advancements.hexalia.mortar_and_pestle.title"),
                        Component.translatable("advancements.hexalia.mortar_and_pestle.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .addCriterion("has_mortar_and_pestle", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MORTAR_AND_PESTLE.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "mortar_and_pestle"), existingFileHelper);

        Advancement stoneDagger = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.STONE_DAGGER.get()),
                        Component.translatable("advancements.hexalia.stone_dagger.title"),
                        Component.translatable("advancements.hexalia.stone_dagger.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(rootAdvancement)
                .addCriterion("has_stone_dagger", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STONE_DAGGER.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "stone_dagger"), existingFileHelper);

        Advancement slimeyBrew = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.BREW_OF_SLIMEWALKER.get()),
                        Component.translatable("advancements.hexalia.slimey_brew.title"),
                        Component.translatable("advancements.hexalia.slimey_brew.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.GOAL,
                        true, true, false))
                .parent(smallCauldron)
                .addCriterion("has_slimewalker_brew", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BREW_OF_SLIMEWALKER.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "slimey_brew"), existingFileHelper);

        Advancement saltItem = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.SALT.get()),
                        Component.translatable("advancements.hexalia.salt_item.title"),
                        Component.translatable("advancements.hexalia.salt_item.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(smallCauldron)
                .addCriterion("has_slimewalker_brew", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SALT.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "salt"), existingFileHelper);

        Advancement purifyingSalts = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.PURIFYING_SALTS.get()),
                        Component.translatable("advancements.hexalia.purifying_salts.title"),
                        Component.translatable("advancements.hexalia.purifying_salts.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.GOAL,
                        true, true, false))
                .parent(saltItem)
                .addCriterion("has_purifying_salts", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.PURIFYING_SALTS.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "purifying_salts"), existingFileHelper);

        Advancement ritualTable = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.RITUAL_TABLE.get()),
                        Component.translatable("advancements.hexalia.ritual_table.title"),
                        Component.translatable("advancements.hexalia.ritual_table.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(saltItem)
                .addCriterion("has_ritual_table", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RITUAL_TABLE.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "ritual_table"), existingFileHelper);

        Advancement hexFocus = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.HEX_FOCUS.get()),
                        Component.translatable("advancements.hexalia.hex_focus.title"),
                        Component.translatable("advancements.hexalia.hex_focus.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(ritualTable)
                .addCriterion("has_hex_focus", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.HEX_FOCUS.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "hex_focus"), existingFileHelper);

        Advancement rabbage = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModItems.RABBAGE.get()),
                        Component.translatable("advancements.hexalia.rabbage.title"),
                        Component.translatable("advancements.hexalia.rabbage.description"),
                        new ResourceLocation(HexaliaMod.MOD_ID, "textures/block/salt_block.png"), FrameType.TASK,
                        true, true, false))
                .parent(ritualTable)
                .addCriterion("has_rabbage", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.RABBAGE.get()))
                .save(saver, new ResourceLocation(HexaliaMod.MOD_ID, "rabbage"), existingFileHelper);
    }
}
