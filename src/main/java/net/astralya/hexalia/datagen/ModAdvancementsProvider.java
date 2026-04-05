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

public class ModAdvancementsProvider extends FabricAdvancementProvider {

    public ModAdvancementsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> writer) {
        Identifier BG = new Identifier(HexaliaMod.MODID, "textures/block/willow_log.png");

        Advancement root = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.HEX_FOCUS),
                        Text.translatable("advancements.hexalia.root.title"),
                        Text.translatable("advancements.hexalia.root.description"),
                        BG,
                        AdvancementFrame.TASK,
                        true, true, false
                ))
                .criterion("has_hex_focus", InventoryChangedCriterion.Conditions.items(ModItems.HEX_FOCUS))
                .build(writer, HexaliaMod.MODID + ":root");

        Advancement saltOfTheCraft = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.SALT),
                        Text.translatable("advancements.hexalia.salt_of_the_craft.title"),
                        Text.translatable("advancements.hexalia.salt_of_the_craft.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(root)
                .criterion("has_salt", InventoryChangedCriterion.Conditions.items(ModItems.SALT))
                .build(writer, HexaliaMod.MODID + ":salt_of_the_craft");

        Advancement crushCourse = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.MORTAR_AND_PESTLE),
                        Text.translatable("advancements.hexalia.crush_course.title"),
                        Text.translatable("advancements.hexalia.crush_course.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(root)
                .criterion("has_mortar", InventoryChangedCriterion.Conditions.items(ModItems.MORTAR_AND_PESTLE))
                .build(writer, HexaliaMod.MODID + ":crush_course");

        Advancement knifeToTreeYou = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.ATHAME),
                        Text.translatable("advancements.hexalia.knife_to_tree_you.title"),
                        Text.translatable("advancements.hexalia.knife_to_tree_you.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(root)
                .criterion("has_athame", InventoryChangedCriterion.Conditions.items(ModItems.ATHAME))
                .build(writer, HexaliaMod.MODID + ":knife_to_tree_you");

        Advancement tableManners = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModBlocks.RITUAL_TABLE.asItem()),
                        Text.translatable("advancements.hexalia.table_manners.title"),
                        Text.translatable("advancements.hexalia.table_manners.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(saltOfTheCraft)
                .criterion("has_ritual_table", InventoryChangedCriterion.Conditions.items(ModBlocks.RITUAL_TABLE))
                .build(writer, HexaliaMod.MODID + ":table_manners");

        Advancement starPower = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.CELESTIAL_CRYSTAL),
                        Text.translatable("advancements.hexalia.star_power.title"),
                        Text.translatable("advancements.hexalia.star_power.description"),
                        null, AdvancementFrame.GOAL, true, true, false
                ))
                .parent(tableManners)
                .criterion("has_celestial_crystal", InventoryChangedCriterion.Conditions.items(ModItems.CELESTIAL_CRYSTAL))
                .build(writer, HexaliaMod.MODID + ":star_power");

        Advancement essenceCollector = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.WATER_NODE),
                        Text.translatable("advancements.hexalia.essence_collector.title"),
                        Text.translatable("advancements.hexalia.essence_collector.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(tableManners)
                .criterion("has_any_node", InventoryChangedCriterion.Conditions.items(
                        ModItems.WATER_NODE, ModItems.AIR_NODE, ModItems.EARTH_NODE, ModItems.FIRE_NODE
                ))
                .build(writer, HexaliaMod.MODID + ":essence_collector");

        Advancement changeOfPlans = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.MUTAVIS),
                        Text.translatable("advancements.hexalia.change_of_plans.title"),
                        Text.translatable("advancements.hexalia.change_of_plans.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(crushCourse)
                .criterion("has_mutavis", InventoryChangedCriterion.Conditions.items(ModItems.MUTAVIS))
                .build(writer, HexaliaMod.MODID + ":change_of_plans");

        Advancement ringOfChange = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModBlocks.MORPHORA.asItem()),
                        Text.translatable("advancements.hexalia.ring_of_change.title"),
                        Text.translatable("advancements.hexalia.ring_of_change.description"),
                        null, AdvancementFrame.GOAL, true, true, false
                ))
                .parent(changeOfPlans)
                .criterion("has_morphora", InventoryChangedCriterion.Conditions.items(ModBlocks.MORPHORA))
                .build(writer, HexaliaMod.MODID + ":ring_of_change");

        Advancement smallBeginnings = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.SMALL_CAULDRON),
                        Text.translatable("advancements.hexalia.small_beginnings.title"),
                        Text.translatable("advancements.hexalia.small_beginnings.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(root)
                .criterion("has_small_cauldron", InventoryChangedCriterion.Conditions.items(ModItems.SMALL_CAULDRON))
                .build(writer, HexaliaMod.MODID + ":small_beginnings");

        Advancement brewbieAward = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.RUSTIC_BOTTLE),
                        Text.translatable("advancements.hexalia.brewbie_award.title"),
                        Text.translatable("advancements.hexalia.brewbie_award.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(smallBeginnings)
                .criterion("obtain_any_brew", InventoryChangedCriterion.Conditions.items(
                        ModItems.RUSTIC_BOTTLE
                ))
                .build(writer, HexaliaMod.MODID + ":brewbie_award");

        Advancement powderAndPouch = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.FOUL_SAC),
                        Text.translatable("advancements.hexalia.powder_and_pouch.title"),
                        Text.translatable("advancements.hexalia.powder_and_pouch.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(brewbieAward)
                .criterion("has_any_sac", InventoryChangedCriterion.Conditions.items(
                        ModItems.FOUL_SAC
                ))
                .build(writer, HexaliaMod.MODID + ":powder_and_pouch");

        Advancement silkenBeginnings = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.SILK_IDOL),
                        Text.translatable("advancements.hexalia.silken_beginnings.title"),
                        Text.translatable("advancements.hexalia.silken_beginnings.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(saltOfTheCraft)
                .criterion("has_silk_idol", InventoryChangedCriterion.Conditions.items(ModItems.SILK_IDOL))
                .build(writer, HexaliaMod.MODID + ":silken_beginnings");

        Advancement pureIntentions = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.PURITY_IDOL),
                        Text.translatable("advancements.hexalia.pure_intentions.title"),
                        Text.translatable("advancements.hexalia.pure_intentions.description"),
                        null, AdvancementFrame.GOAL, true, true, false
                ))
                .parent(silkenBeginnings)
                .criterion("use_purity_idol", ConsumeItemCriterion.Conditions.item(ModItems.PURITY_IDOL))
                .build(writer, HexaliaMod.MODID + ":pure_intentions");

        Advancement kelpYourself = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.KELPWEAVE_BLADE),
                        Text.translatable("advancements.hexalia.kelp_yourself.title"),
                        Text.translatable("advancements.hexalia.kelp_yourself.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(tableManners)
                .criterion("has_kelpweave_blade", InventoryChangedCriterion.Conditions.items(ModItems.KELPWEAVE_BLADE))
                .build(writer, HexaliaMod.MODID + ":kelp_yourself");

        Advancement wiseInvestment = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.SAGE_PENDANT),
                        Text.translatable("advancements.hexalia.wise_investment.title"),
                        Text.translatable("advancements.hexalia.wise_investment.description"),
                        null, AdvancementFrame.TASK, true, true, false
                ))
                .parent(tableManners)
                .criterion("has_sage_pendant", InventoryChangedCriterion.Conditions.items(ModItems.SAGE_PENDANT))
                .build(writer, HexaliaMod.MODID + ":wise_investment");

        Advancement herbNerd = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModBlocks.SPIRIT_BLOOM.asItem()),
                        Text.translatable("advancements.hexalia.herb_nerd.title"),
                        Text.translatable("advancements.hexalia.herb_nerd.description"),
                        null, AdvancementFrame.CHALLENGE, true, true, false
                ))
                .parent(crushCourse)
                .criterion("dreamshroom", InventoryChangedCriterion.Conditions.items(ModBlocks.DREAMSHROOM))
                .criterion("spirit_bloom", InventoryChangedCriterion.Conditions.items(ModBlocks.SPIRIT_BLOOM))
                .criterion("ghost_fern", InventoryChangedCriterion.Conditions.items(ModBlocks.GHOST_FERN))
                .criterion("celestial_bloom", InventoryChangedCriterion.Conditions.items(ModBlocks.CELESTIAL_BLOOM))
                .criterion("siren_kelp", InventoryChangedCriterion.Conditions.items(ModItems.SIREN_KELP))
                .build(writer, HexaliaMod.MODID + ":herb_nerd");

        Advancement seasonedFarmer = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.MANDRAKE),
                        Text.translatable("advancements.hexalia.seasoned_farmer.title"),
                        Text.translatable("advancements.hexalia.seasoned_farmer.description"),
                        null, AdvancementFrame.CHALLENGE, true, true, false
                ))
                .parent(crushCourse)
                .criterion("mandrake", InventoryChangedCriterion.Conditions.items(ModItems.MANDRAKE))
                .criterion("sunfire_tomato", InventoryChangedCriterion.Conditions.items(ModItems.SUNFIRE_TOMATO))
                .criterion("galeberries", InventoryChangedCriterion.Conditions.items(ModItems.GALEBERRIES))
                .criterion("saltsprout", InventoryChangedCriterion.Conditions.items(ModItems.SALTSPROUT))
                .criterion("chillberries", InventoryChangedCriterion.Conditions.items(ModItems.CHILLBERRIES))
                .build(writer, HexaliaMod.MODID + ":seasoned_farmer");

        Advancement masterOrNot = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.TEMPEST_IDOL),
                        Text.translatable("advancements.hexalia.master_or_not.title"),
                        Text.translatable("advancements.hexalia.master_or_not.description"),
                        null, AdvancementFrame.GOAL, true, true, false
                ))
                .parent(silkenBeginnings)
                .criterion("use_rain", ConsumeItemCriterion.Conditions.item(ModItems.RAINFALL_IDOL))
                .criterion("use_clear", ConsumeItemCriterion.Conditions.item(ModItems.CLARITY_IDOL))
                .criterion("use_storm", ConsumeItemCriterion.Conditions.item(ModItems.TEMPEST_IDOL))
                .build(writer, HexaliaMod.MODID + ":master_or_not");

        Advancement brewedAwakening = Advancement.Builder.create()
                .display(new AdvancementDisplay(
                        new ItemStack(ModItems.BREW_OF_DAYBLOOM),
                        Text.translatable("advancements.hexalia.brewed_awakening.title"),
                        Text.translatable("advancements.hexalia.brewed_awakening.description"),
                        null, AdvancementFrame.CHALLENGE, true, true, false
                ))
                .parent(brewbieAward)
                .criterion("slimewalker", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_SLIMEWALKER))
                .criterion("bloodlust", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_BLOODLUST))
                .criterion("spikeskin", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_SPIKESKIN))
                .criterion("homestead", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_HOMESTEAD))
                .criterion("siphoning", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_SIPHON))
                .criterion("daybloom", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_DAYBLOOM))
                .criterion("arachnid_grace", ConsumeItemCriterion.Conditions.item(ModItems.BREW_OF_ARACHNID_GRACE))
                .build(writer, HexaliaMod.MODID + ":brewed_awakening");
    }
}