package net.astralya.hexalia.datagen;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.datagen.custom.MutationRecipeBuilder;
import net.astralya.hexalia.datagen.custom.RitualBrazierRecipeBuilder;
import net.astralya.hexalia.datagen.custom.SmallCauldronRecipeBuilder;
import net.astralya.hexalia.datagen.custom.RitualTableRecipeBuilder;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {

        // Shaped Recipe for Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON.get())
                .pattern("S S")
                .pattern("P P")
                .pattern("SSS")
                .define('P', Items.COPPER_INGOT)
                .define('S', Items.COBBLED_DEEPSLATE)
                .unlockedBy("has_copper_ingot",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.COPPER_INGOT).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.DREAMCATCHER.get())
                .pattern(" S ")
                .pattern("SPS")
                .pattern("ATA")
                .define('P', Items.STRING)
                .define('S', Items.STICK)
                .define('A', Items.FEATHER)
                .define('T', ModItems.FIRE_NODE.get())
                .unlockedBy("has_stick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SALT_LAMP.get())
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .define('P', Items.TORCH)
                .define('S', ModTags.Items.SALT)
                .define('A', Items.COPPER_INGOT)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.RUSTIC_BOTTLE.get(), 3)
                .pattern("S S")
                .pattern(" P ")
                .define('P', Items.CLAY_BALL)
                .define('S', Blocks.GLASS)
                .unlockedBy("has_clay_ball",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.INFUSED_DIRT.get(), 2)
                .pattern("SP")
                .pattern("PS")
                .define('P', Blocks.DIRT)
                .define('S', ModItems.SIREN_KELP.get())
                .unlockedBy("has_siren_kelp",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SIREN_KELP.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ATHAME.get())
                .pattern(" S")
                .pattern("P ")
                .define('S', Items.FLINT)
                .define('P', Items.STICK)
                .unlockedBy("has_cobblestone",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLESTONE).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RUSTIC_OVEN.get())
                .pattern("PPP")
                .pattern("SAS")
                .pattern("SSS")
                .define('P', Items.IRON_INGOT)
                .define('S', Items.COBBLED_DEEPSLATE)
                .define('A', ItemTags.COALS)
                .unlockedBy("has_cobbled_deepslate",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLED_DEEPSLATE).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SHELF.get())
                .pattern(" P ")
                .pattern("S S")
                .define('P', Items.COBBLED_DEEPSLATE_SLAB)
                .define('S', Items.STICK)
                .unlockedBy("has_cobbled_deepslate",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLED_DEEPSLATE_SLAB).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CENSER.get())
                .pattern(" P ")
                .pattern("PAP")
                .pattern("SSS")
                .define('P', Items.BRICK)
                .define('S', ItemTags.LOGS)
                .define('A', ItemTags.COALS)
                .unlockedBy("has_brick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BRICK).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEX_FOCUS.get())
                .pattern("  S")
                .pattern(" P ")
                .pattern("A  ")
                .define('P', ItemTags.LEAVES)
                .define('S', Items.AMETHYST_SHARD)
                .define('A', Items.STICK)
                .unlockedBy("has_amethyst_shard",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CANDLE_SKULL.get())
                .pattern("P")
                .pattern("S")
                .define('P', Items.CANDLE)
                .define('S', Items.SKELETON_SKULL)
                .unlockedBy("has_skeleton_skull",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.SKELETON_SKULL).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WITHER_CANDLE_SKULL.get())
                .pattern("P")
                .pattern("S")
                .define('P', Items.CANDLE)
                .define('S', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_skeleton_skull",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.WITHER_SKELETON_SKULL).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SILK_IDOL.get())
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('S', ModItems.SILK_FIBER.get())
                .define('P', ModTags.Items.CRUSHED_HERBS)
                .unlockedBy("has_silk_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_FIBER.get()).build()))
                .save(recipeConsumer);

        // Recipes for Vanilla Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', Items.ROTTEN_FLESH)
                .define('S', ModTags.Items.SALT)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "leather_from_salt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', ModItems.SILK_FIBER.get())
                .define('S', Items.STRING)
                .unlockedBy("has_silk_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_FIBER.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "cobweb_from_fiber"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE)
                .requires(ModBlocks.LAVENDER.get())
                .unlockedBy("has_lavender",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.LAVENDER.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "purple_dye_from_begonia"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE)
                .requires(ModBlocks.BEGONIA.get())
                .unlockedBy("has_begonia",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.BEGONIA.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "pink_dye_from_begonia"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLACK_DYE)
                .requires(ModBlocks.NIGHTSHADE_BUSH.get())
                .unlockedBy("has_nightshade_bush",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.NIGHTSHADE_BUSH.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "black_dye_from_nightshade"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ORANGE_DYE)
                .requires(ModBlocks.DAHLIA.get())
                .unlockedBy("has_dahlia",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.DAHLIA.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "orange_dye_from_dahlia"));

        // Reversible Compacting Recipes for Blocks
        nineBlockStorageRecipes(recipeConsumer, RecipeCategory.BUILDING_BLOCKS, ModItems.SALT.get(),
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_BLOCK.get(),
                "hexalia:salt", "salt","hexalia:salt_block", "salt");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                .pattern("PP")
                .pattern("PP")
                .define('P', ModItems.CELESTIAL_CRYSTAL.get())
                .unlockedBy("has_celestial_crystal",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CELESTIAL_CRYSTAL.get()).build()))
                .save(recipeConsumer);

        // Armor Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOSTVEIL.get())
                .pattern("A A")
                .pattern("PAP")
                .pattern("SSS")
                .define('P', ModItems.SILK_FIBER.get())
                .define('S', ModBlocks.GHOST_FERN.get())
                .define('A', Items.LEATHER)
                .unlockedBy("has_ghost_fern",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.GHOST_FERN.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.EARPLUGS.get())
                .pattern("P P")
                .define('P', Items.LEATHER)
                .unlockedBy("has_leather",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.LEATHER).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BOGGED_BOOTS.get())
                .pattern("PSP")
                .pattern("A A")
                .define('S', ModItems.SILK_FIBER.get())
                .define('P', ModBlocks.WITCHWEED.get())
                .define('A', Items.DRIED_KELP)
                .unlockedBy("has_ghost_fern",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WITCHWEED.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PURIFYING_SAC.get())
                .pattern(" S ")
                .pattern("PAP")
                .pattern(" P ")
                .define('S', ModTags.Items.SALT)
                .define('A', ModItems.LOTUS_BLOSSOM.get())
                .define('P', Items.LEATHER)
                .unlockedBy("has_salt", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.FROST_SAC.get())
                .pattern(" S ")
                .pattern("PAP")
                .pattern(" P ")
                .define('S', Items.SNOWBALL)
                .define('A', ModItems.CHILLBERRIES.get())
                .define('P', Items.LEATHER)
                .unlockedBy("has_chillberries", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CHILLBERRIES.get()).build()))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.FOUL_SAC.get())
                .pattern(" S ")
                .pattern("PAP")
                .pattern(" P ")
                .define('S', Items.SPIDER_EYE)
                .define('A', ModBlocks.WITCHWEED.get())
                .define('P', Items.LEATHER)
                .unlockedBy("has_witchweed", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WITCHWEED.get().asItem()).build()))
                .save(recipeConsumer);

        // Shapeless Recipes for Seeds
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANDRAKE_SEEDS.get())
                .requires(ModItems.MANDRAKE.get())
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUNFIRE_TOMATO_SEEDS.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(recipeConsumer);

        // Shapeless Recipes for Items & Blocks
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHILLBERRY_PIE.get())
                .requires(ModItems.CHILLBERRIES.get())
                .requires(Items.EGG)
                .requires(Items.SUGAR)
                .requires(Items.WHEAT)
                .unlockedBy("has_chillberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CHILLBERRIES.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SPICY_SANDWICH.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .requires(ModTags.Items.BREAD)
                .requires(ModTags.Items.COOKED_MEATS)
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.MANDRAKE_STEW.get())
                .requires(ModItems.MANDRAKE.get())
                .requires(Items.BOWL)
                .requires(ModTags.Items.VEGETABLES)
                .requires(ModTags.Items.VEGETABLES)
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SILK_FIBER.get(), 2)
                .requires(ModItems.SILKWORM.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy("has_silkworm",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILKWORM.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GALEBERRIES_COOKIE.get(), 4)
                .requires(ModItems.GALEBERRIES.get())
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.SUGAR)
                .unlockedBy("has_galeberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.GALEBERRIES.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.CLARITY_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.AIR_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .requires(Items.SUNFLOWER)
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.RAINFALL_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .requires(Blocks.BLUE_ORCHID)
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.TEMPEST_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.FIRE_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.PURITY_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.LOTUS_BLOSSOM.get())
                .requires(ModItems.SALT.get())
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CELESTIAL_CRYSTAL.get(), 4)
                .requires(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                .unlockedBy("has_celestial_crystal_block",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get()).build()))
                .save(recipeConsumer);

        // Shapeless Recipes for Mortar & Pestle and Refined Resources
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MORTAR_AND_PESTLE.get())
                .requires(Items.BOWL)
                .requires(Items.STONE)
                .unlockedBy("has_bowl",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOWL).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SPIRIT_POWDER.get())
                .requires(ModBlocks.SPIRIT_BLOOM.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VERDANT_GRIMOIRE.get())
                .requires(Items.BOOK)
                .requires(ModTags.Items.HERBS)
                .unlockedBy("has_book",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOOK).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SIREN_PASTE.get())
                .requires(ModItems.SIREN_KELP.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DREAM_PASTE.get())
                .requires(ModBlocks.DREAMSHROOM.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GHOST_POWDER.get())
                .requires(ModBlocks.GHOST_FERN.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STRING, 3)
                .requires(ModItems.SILK_FIBER.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "string_from_mortar_and_pestle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SALT.get())
                .requires(ModItems.SALTSPROUT.get())
                .requires(ModItems.MORTAR_AND_PESTLE.get())
                .unlockedBy("has_mortar_and_pestle",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MORTAR_AND_PESTLE.get()).build()))
                .save(recipeConsumer, new ResourceLocation("hexalia", "salt_from_mortar_and_pestle"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MUTAVIS.get())
                .requires(Items.BONE_MEAL)
                .requires(ModItems.TREE_RESIN.get())
                .requires(ModTags.Items.CRUSHED_HERBS)
                .requires(ModTags.Items.CRUSHED_HERBS)
                .unlockedBy("has_bone_meal",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BONE_MEAL).build()))
                .save(recipeConsumer);

        // Recipes for Small Cauldron Brews
        new SmallCauldronRecipeBuilder(List.of(Blocks.CACTUS, ModItems.MANDRAKE.get(), ModItems.GHOST_POWDER.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SPIKESKIN.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(Items.BEEF, ModItems.SIREN_PASTE.get(), ModItems.SALTSPROUT.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_BLOODLUST.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(Items.SLIME_BALL, Items.FEATHER, Items.SPIDER_EYE),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SLIMEWALKER.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(Items.ENDER_PEARL, ModItems.TREE_RESIN.get(), ModItems.SPIRIT_POWDER.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_HOMESTEAD.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(Items.FLINT, Items.GOLD_NUGGET, ModItems.MANDRAKE.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_SIPHON.get())
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(ModItems.GALEBERRIES.get(), ModItems.SUNFIRE_TOMATO.get(), ModItems.SPIRIT_POWDER.get()),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_DAYBLOOM.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);
        new SmallCauldronRecipeBuilder(List.of(ModItems.DREAM_PASTE.get(), Items.SPIDER_EYE, Items.BLACK_DYE),
                ModItems.RUSTIC_BOTTLE.get(), ModItems.BREW_OF_ARACHNID_GRACE.get())
                .experience(5.0f)
                .brewTime(175)
                .unlockedBy("has_rustic_bottle", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.RUSTIC_BOTTLE.get()).build()))
                .save(recipeConsumer);

        // Ritual Table Recipes
        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModBlocks.GRIMSHADE.get()))
                .tableInput(Blocks.AZURE_BLUET.asItem())
                .brazier(ModItems.GHOST_POWDER.get())
                .brazier(Items.WITHER_ROSE)
                .brazier(Items.BONE)
                .brazier(Items.BLACK_DYE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.GRIMSHADE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.RABBAGE_SEEDS.get()))
                .tableInput(Items.BEETROOT_SEEDS)
                .brazier(ModItems.DREAM_PASTE.get())
                .brazier(Items.IRON_NUGGET)
                .brazier(Items.SWEET_BERRIES)
                .brazier(Blocks.POPPY.asItem())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.RABBAGE_SEEDS.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.SAGE_PENDANT.get()))
                .tableInput(ModItems.CELESTIAL_CRYSTAL.get())
                .brazier(ModItems.SPIRIT_POWDER.get())
                .brazier(Items.GOLD_NUGGET)
                .brazier(Items.BOOK)
                .brazier(Items.EXPERIENCE_BOTTLE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.SAGE_PENDANT.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModBlocks.MORPHORA.get()))
                .tableInput(Blocks.POPPY.asItem())
                .brazier(ModItems.DREAM_PASTE.get())
                .brazier(ModItems.SPIRIT_POWDER.get())
                .brazier(ModItems.EARTH_NODE.get())
                .brazier(ModItems.TREE_RESIN.get())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.MORPHORA.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.KELPWEAVE_BLADE.get()))
                .tableInput(ModItems.ANCIENT_SEED.get())
                .brazier(ModItems.SIREN_PASTE.get())
                .brazier(ModItems.WATER_NODE.get())
                .brazier(Items.IRON_NUGGET)
                .brazier(Items.KELP)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.KELPWEAVE_BLADE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModBlocks.NAUTILITE.get()))
                .tableInput(Items.KELP)
                .brazier(ModItems.SIREN_PASTE.get())
                .brazier(ModItems.WATER_NODE.get())
                .brazier(Items.NAUTILUS_SHELL)
                .brazier(Items.PRISMARINE_CRYSTALS)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.NAUTILITE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModBlocks.WINDSONG.get()))
                .tableInput(Blocks.OXEYE_DAISY.asItem())
                .brazier(ModItems.AIR_NODE.get())
                .brazier(ModItems.GHOST_POWDER.get())
                .brazier(Items.FEATHER)
                .brazier(Items.PHANTOM_MEMBRANE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.WINDSONG.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModBlocks.ASTRYLIS.get()))
                .tableInput(Blocks.LILY_OF_THE_VALLEY.asItem())
                .brazier(ModItems.CELESTIAL_CRYSTAL.get())
                .brazier(ModItems.EARTH_NODE.get())
                .brazier(Items.BONE_MEAL)
                .brazier(Items.GLOWSTONE_DUST)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.ASTRYLIS.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.FIRE_NODE.get()))
                .tableInput(Items.AMETHYST_SHARD)
                .brazier(Items.COAL)
                .brazier(Blocks.SUNFLOWER.asItem())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.FIRE_NODE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.AIR_NODE.get()))
                .tableInput(Items.AMETHYST_SHARD)
                .brazier(Items.FEATHER)
                .brazier(Blocks.DANDELION.asItem())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.AIR_NODE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.WATER_NODE.get()))
                .tableInput(Items.AMETHYST_SHARD)
                .brazier(Blocks.LILY_PAD.asItem())
                .brazier(Items.INK_SAC)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.WATER_NODE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTable(new ItemStack(ModItems.EARTH_NODE.get()))
                .tableInput(Items.AMETHYST_SHARD)
                .brazier(Blocks.BROWN_MUSHROOM.asItem())
                .brazier(Items.CLAY_BALL)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.EARTH_NODE.getId().getPath() + "_from_ritual_table"));

        // Recipes for Celestial Ritual Items
        new RitualBrazierRecipeBuilder(Items.GLOW_BERRIES, ModItems.GALEBERRIES.get())
                .unlockedBy("has_glow_berries", has(Items.GLOW_BERRIES))
                .save(recipeConsumer);
        new RitualBrazierRecipeBuilder(Items.AMETHYST_SHARD, ModItems.CELESTIAL_CRYSTAL.get())
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(recipeConsumer);
        new RitualBrazierRecipeBuilder(Blocks.AMETHYST_BLOCK, ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                .unlockedBy("has_amethyst_block", has(Blocks.AMETHYST_BLOCK))
                .save(recipeConsumer);

        // Mutation Recipes
        MutationRecipeBuilder.mutation(Ingredient.of(Items.BLUE_ORCHID), new ItemStack(ModBlocks.SPIRIT_BLOOM.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.SPIRIT_BLOOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Items.KELP), new ItemStack(ModBlocks.SIREN_KELP.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.SIREN_KELP.getId().getPath() + "_from_mutation"));

        Ingredient anyTulip = Ingredient.of(ModTags.Items.TULIPS);
        MutationRecipeBuilder.mutation(anyTulip, new ItemStack(ModBlocks.CELESTIAL_BLOOM.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.CELESTIAL_BLOOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Items.BROWN_MUSHROOM), new ItemStack(ModBlocks.DREAMSHROOM.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.DREAMSHROOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Items.FERN), new ItemStack(ModBlocks.GHOST_FERN.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.GHOST_FERN.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Items.LILY_PAD), new ItemStack(ModBlocks.LOTUS_FLOWER.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModBlocks.LOTUS_FLOWER.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.DIORITE), new ItemStack(Blocks.GRANITE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "granite_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.GRANITE), new ItemStack(Blocks.ANDESITE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "andesite_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.ANDESITE), new ItemStack(Blocks.DIORITE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "diorite_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.DRIPSTONE_BLOCK), new ItemStack(Blocks.TUFF))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "tuff_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.ICE), new ItemStack(Blocks.BLUE_ICE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "blue_ice_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.SAND), new ItemStack(Blocks.RED_SAND))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "red_sand_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.CLAY), new ItemStack(Blocks.MUD))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "mud_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.SNOW_BLOCK), new ItemStack(Blocks.PACKED_ICE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "packed_ice_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.NETHERRACK), new ItemStack(Blocks.BLACKSTONE))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "blackstone_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.DIRT), new ItemStack(Blocks.ROOTED_DIRT))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "rooted_dirt_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.ROOTED_DIRT), new ItemStack(Blocks.PODZOL))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, "podzol_from_mutation"));

        MutationRecipeBuilder.mutation(Ingredient.of(Blocks.CACTUS), new ItemStack(ModItems.SALTSPROUT.get()))
                .unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeConsumer, new ResourceLocation(HexaliaMod.MODID, ModItems.SALTSPROUT.getId().getPath() + "_from_mutation"));

        // Recipes for Wood-related Blocks
        planksFromLog(recipeConsumer, ModBlocks.COTTONWOOD_PLANKS.get(), ModTags.Items.COTTONWOOD_LOGS, 4);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.COTTONWOOD_BUTTON.get())
                .requires(ModBlocks.COTTONWOOD_PLANKS.get())
                .unlockedBy("has_cottonwood_planks",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);

        trapdoorBuilder(ModBlocks.COTTONWOOD_TRAPDOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        doorBuilder(ModBlocks.COTTONWOOD_DOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        stairBuilder(ModBlocks.COTTONWOOD_STAIRS.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.COTTONWOOD_SLAB.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        fenceBuilder(ModBlocks.COTTONWOOD_FENCE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        fenceGateBuilder(ModBlocks.COTTONWOOD_FENCE_GATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        signBuilder(ModBlocks.COTTONWOOD_SIGN.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeConsumer);
        hangingSign(recipeConsumer, ModItems.COTTONWOOD_HANGING_SIGN.get(), ModBlocks.STRIPPED_COTTONWOOD_LOG.get());

        planksFromLog(recipeConsumer, ModBlocks.WILLOW_PLANKS.get(), ModTags.Items.WILLOW_LOGS, 4);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.WILLOW_BUTTON.get())
                .requires(ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy("has_willow_planks",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);

        trapdoorBuilder(ModBlocks.WILLOW_TRAPDOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        doorBuilder(ModBlocks.WILLOW_DOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.WILLOW_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        stairBuilder(ModBlocks.WILLOW_STAIRS.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.WILLOW_SLAB.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        fenceBuilder(ModBlocks.WILLOW_FENCE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        fenceGateBuilder(ModBlocks.WILLOW_FENCE_GATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        signBuilder(ModBlocks.WILLOW_SIGN.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeConsumer);
        hangingSign(recipeConsumer, ModItems.WILLOW_HANGING_SIGN.get(), ModBlocks.STRIPPED_WILLOW_LOG.get());
    }
}
