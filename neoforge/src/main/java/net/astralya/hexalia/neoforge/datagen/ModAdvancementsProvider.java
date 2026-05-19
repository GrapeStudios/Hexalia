package net.astralya.hexalia.neoforge.datagen;

import java.util.Optional;
import java.util.function.Consumer;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ModAdvancementsProvider implements AdvancementSubProvider {
  private static final ResourceLocation BACKGROUND =
      ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "textures/block/willow_log.png");

  @Override
  public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer) {
    AdvancementHolder root =
        Advancement.Builder.advancement()
            .display(display(ModItems.HEX_FOCUS.get(), "root", Optional.of(BACKGROUND), AdvancementType.TASK))
            .addCriterion("has_hex_focus", has(ModItems.HEX_FOCUS.get()))
            .save(writer, id("root"));

    AdvancementHolder saltOfTheCraft =
        advancement(ModItems.SALT.get(), "salt_of_the_craft", AdvancementType.TASK)
            .parent(root)
            .addCriterion("has_salt", has(ModItems.SALT.get()))
            .save(writer, id("salt_of_the_craft"));

    AdvancementHolder crushCourse =
        advancement(ModItems.MORTAR_AND_PESTLE.get(), "crush_course", AdvancementType.TASK)
            .parent(root)
            .addCriterion("has_mortar", has(ModItems.MORTAR_AND_PESTLE.get()))
            .save(writer, id("crush_course"));

    AdvancementHolder knifeToTreeYou =
        advancement(ModItems.ATHAME.get(), "knife_to_tree_you", AdvancementType.TASK)
            .parent(root)
            .addCriterion("has_athame", has(ModItems.ATHAME.get()))
            .save(writer, id("knife_to_tree_you"));

    AdvancementHolder tableManners =
        advancement(ModBlocks.RITUAL_TABLE.get(), "table_manners", AdvancementType.TASK)
            .parent(saltOfTheCraft)
            .addCriterion("has_ritual_table", has(ModBlocks.RITUAL_TABLE.get()))
            .save(writer, id("table_manners"));

    AdvancementHolder starPower =
        advancement(ModItems.CELESTIAL_CRYSTAL.get(), "star_power", AdvancementType.GOAL)
            .parent(tableManners)
            .addCriterion("has_celestial_crystal", has(ModItems.CELESTIAL_CRYSTAL.get()))
            .save(writer, id("star_power"));

    AdvancementHolder essenceCollector =
        advancement(ModItems.WATER_NODE.get(), "essence_collector", AdvancementType.TASK)
            .parent(tableManners)
            .addCriterion(
                "has_any_node",
                has(
                    ModItems.WATER_NODE.get(),
                    ModItems.AIR_NODE.get(),
                    ModItems.EARTH_NODE.get(),
                    ModItems.FIRE_NODE.get()))
            .save(writer, id("essence_collector"));

    AdvancementHolder changeOfPlans =
        advancement(ModItems.MUTAVIS.get(), "change_of_plans", AdvancementType.TASK)
            .parent(crushCourse)
            .addCriterion("has_mutavis", has(ModItems.MUTAVIS.get()))
            .save(writer, id("change_of_plans"));

    AdvancementHolder ringOfChange =
        advancement(ModItems.MORPHORA.get(), "ring_of_change", AdvancementType.GOAL)
            .parent(changeOfPlans)
            .addCriterion("has_morphora", has(ModItems.MORPHORA.get()))
            .save(writer, id("ring_of_change"));

    AdvancementHolder smallBeginnings =
        advancement(ModItems.SMALL_CAULDRON.get(), "small_beginnings", AdvancementType.TASK)
            .parent(root)
            .addCriterion("has_small_cauldron", has(ModItems.SMALL_CAULDRON.get()))
            .save(writer, id("small_beginnings"));

    AdvancementHolder brewbieAward =
        advancement(ModItems.RUSTIC_BOTTLE.get(), "brewbie_award", AdvancementType.TASK)
            .parent(smallBeginnings)
            .addCriterion("has_rustic_bottle", has(ModItems.RUSTIC_BOTTLE.get()))
            .save(writer, id("brewbie_award"));

    advancement(ModItems.FOUL_SAC.get(), "powder_and_pouch", AdvancementType.TASK)
        .parent(brewbieAward)
        .addCriterion("has_foul_sac", has(ModItems.FOUL_SAC.get()))
        .save(writer, id("powder_and_pouch"));

    AdvancementHolder silkenBeginnings =
        advancement(ModItems.SILK_IDOL.get(), "silken_beginnings", AdvancementType.TASK)
            .parent(saltOfTheCraft)
            .addCriterion("has_silk_idol", has(ModItems.SILK_IDOL.get()))
            .save(writer, id("silken_beginnings"));

    advancement(ModItems.PURITY_IDOL.get(), "pure_intentions", AdvancementType.GOAL)
        .parent(silkenBeginnings)
        .addCriterion("use_purity_idol", used(ModItems.PURITY_IDOL.get()))
        .save(writer, id("pure_intentions"));

    advancement(ModItems.KELPWEAVE_BLADE.get(), "kelp_yourself", AdvancementType.TASK)
        .parent(tableManners)
        .addCriterion("has_kelpweave_blade", has(ModItems.KELPWEAVE_BLADE.get()))
        .save(writer, id("kelp_yourself"));

    advancement(ModItems.SAGE_PENDANT.get(), "wise_investment", AdvancementType.TASK)
        .parent(tableManners)
        .addCriterion("has_sage_pendant", has(ModItems.SAGE_PENDANT.get()))
        .save(writer, id("wise_investment"));

    advancement(ModBlocks.SPIRIT_BLOOM.get(), "herb_nerd", AdvancementType.CHALLENGE)
        .parent(crushCourse)
        .addCriterion("dreamshroom", has(ModBlocks.DREAMSHROOM.get()))
        .addCriterion("spirit_bloom", has(ModBlocks.SPIRIT_BLOOM.get()))
        .addCriterion("ghost_fern", has(ModBlocks.GHOST_FERN.get()))
        .addCriterion("celestial_bloom", has(ModBlocks.CELESTIAL_BLOOM.get()))
        .addCriterion("siren_kelp", has(ModItems.SIREN_KELP.get()))
        .save(writer, id("herb_nerd"));

    advancement(ModItems.MANDRAKE.get(), "seasoned_farmer", AdvancementType.CHALLENGE)
        .parent(crushCourse)
        .addCriterion("mandrake", has(ModItems.MANDRAKE.get()))
        .addCriterion("sunfire_tomato", has(ModItems.SUNFIRE_TOMATO.get()))
        .addCriterion("galeberries", has(ModItems.GALEBERRIES.get()))
        .addCriterion("saltsprout", has(ModItems.SALTSPROUT.get()))
        .addCriterion("chillberries", has(ModItems.CHILLBERRIES.get()))
        .save(writer, id("seasoned_farmer"));

    advancement(ModItems.TEMPEST_IDOL.get(), "master_or_not", AdvancementType.GOAL)
        .parent(silkenBeginnings)
        .addCriterion("use_rain", used(ModItems.RAINFALL_IDOL.get()))
        .addCriterion("use_clear", used(ModItems.CLARITY_IDOL.get()))
        .addCriterion("use_storm", used(ModItems.TEMPEST_IDOL.get()))
        .save(writer, id("master_or_not"));

    advancement(ModItems.BREW_OF_DAYBLOOM.get(), "brewed_awakening", AdvancementType.CHALLENGE)
        .parent(brewbieAward)
        .addCriterion("slimewalker", used(ModItems.BREW_OF_SLIMEWALKER.get()))
        .addCriterion("bloodlust", used(ModItems.BREW_OF_BLOODLUST.get()))
        .addCriterion("spikeskin", used(ModItems.BREW_OF_SPIKESKIN.get()))
        .addCriterion("homestead", used(ModItems.BREW_OF_HOMESTEAD.get()))
        .addCriterion("siphoning", used(ModItems.BREW_OF_SIPHON.get()))
        .addCriterion("daybloom", used(ModItems.BREW_OF_DAYBLOOM.get()))
        .addCriterion("arachnid_grace", used(ModItems.BREW_OF_ARACHNID_GRACE.get()))
        .addCriterion("hollow_silence", used(ModItems.BREW_OF_HOLLOW_SILENCE.get()))
        .save(writer, id("brewed_awakening"));
  }

  private static Advancement.Builder advancement(
      ItemLike icon, String name, AdvancementType type) {
    return Advancement.Builder.advancement().display(display(icon, name, Optional.empty(), type));
  }

  private static DisplayInfo display(
      ItemLike icon, String name, Optional<ResourceLocation> background, AdvancementType type) {
    return new DisplayInfo(
        new ItemStack(icon),
        Component.translatable("advancements.hexalia." + name + ".title"),
        Component.translatable("advancements.hexalia." + name + ".description"),
        background,
        type,
        true,
        true,
        false);
  }

  private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike... items) {
    return InventoryChangeTrigger.TriggerInstance.hasItems(items);
  }

  private static Criterion<ConsumeItemTrigger.TriggerInstance> used(Item item) {
    return ConsumeItemTrigger.TriggerInstance.usedItem(item);
  }

  private static String id(String path) {
    return ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, path).toString();
  }
}
