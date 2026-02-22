package net.astralya.hexalia.datagen;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.datagen.custom.*;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Shaped Recipe for Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON.get())
                .pattern("D D")
                .pattern("DCD")
                .pattern("LLL")
                .define('C', ItemTags.COALS)
                .define('D', Items.COBBLED_DEEPSLATE)
                .define('L', ItemTags.LOGS)
                .unlockedBy("has_cobbled_deepslate",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLED_DEEPSLATE).build()))
                .save(recipeOutput);

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
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SALT_LAMP.get())
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .define('P', Items.TORCH)
                .define('S', ModTags.Items.SALT)
                .define('A', Items.COPPER_INGOT)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.RUSTIC_BOTTLE.get(), 3)
                .pattern("S S")
                .pattern(" P ")
                .define('P', Items.CLAY_BALL)
                .define('S', Blocks.GLASS)
                .unlockedBy("has_clay_ball",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.CLAY_BALL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.INFUSED_DIRT.get(), 2)
                .pattern("SP")
                .pattern("PS")
                .define('P', Blocks.DIRT)
                .define('S', ModItems.SIREN_KELP.get())
                .unlockedBy("has_siren_kelp",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SIREN_KELP.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ATHAME.get())
                .pattern(" S")
                .pattern("P ")
                .define('S', Items.FLINT)
                .define('P', Items.STICK)
                .unlockedBy("has_stick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.LADLE.get())
                .pattern("  B")
                .pattern(" S ")
                .pattern("S  ")
                .define('S', Items.STICK)
                .define('B', Items.BOWL)
                .unlockedBy("has_stick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STICK).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.SHELF.get())
                .pattern(" P ")
                .pattern("S S")
                .define('P', Items.COBBLED_DEEPSLATE_SLAB)
                .define('S', Items.STICK)
                .unlockedBy("has_cobbled_deepslate",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Blocks.COBBLED_DEEPSLATE_SLAB).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CENSER.get())
                .pattern(" P ")
                .pattern("PAP")
                .pattern("SSS")
                .define('P', Items.BRICK)
                .define('S', ItemTags.LOGS)
                .define('A', ItemTags.COALS)
                .unlockedBy("has_brick",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BRICK).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEX_FOCUS.get())
                .pattern("  S")
                .pattern(" P ")
                .pattern("A  ")
                .define('P', ItemTags.LEAVES)
                .define('S', Items.AMETHYST_SHARD)
                .define('A', Items.STICK)
                .unlockedBy("has_amethyst_shard",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.AMETHYST_SHARD).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CANDLE_SKULL.get())
                .pattern("P")
                .pattern("S")
                .define('P', Items.CANDLE)
                .define('S', Items.SKELETON_SKULL)
                .unlockedBy("has_skeleton_skull",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.SKELETON_SKULL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WITHER_CANDLE_SKULL.get())
                .pattern("P")
                .pattern("S")
                .define('P', Items.CANDLE)
                .define('S', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_skeleton_skull",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.WITHER_SKELETON_SKULL).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SILK_IDOL.get())
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('S', ModItems.SILK_FIBER.get())
                .define('P', ModTags.Items.CRUSHED_HERBS)
                .unlockedBy("has_silk_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_FIBER.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.NESTING_BLOCK.get())
                .pattern("SSS")
                .pattern("PNP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('N', ItemTags.LEAVES)
                .define('S', Items.STRING)
                .unlockedBy("has_string",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.STRING).build()))
                .save(recipeOutput);

        // Recipes for Vanilla Items & Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.LEATHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', Items.ROTTEN_FLESH)
                .define('S', ModTags.Items.SALT)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "leather_from_salt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" S ")
                .define('P', ModItems.SILK_FIBER.get())
                .define('S', Items.STRING)
                .unlockedBy("has_silk_fiber",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_FIBER.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "cobweb_from_fiber"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE)
                .requires(ModBlocks.LAVENDER.get())
                .unlockedBy("has_lavender",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.LAVENDER.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "purple_dye_from_begonia"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE)
                .requires(ModBlocks.BEGONIA.get())
                .unlockedBy("has_begonia",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.BEGONIA.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "pink_dye_from_begonia"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BLACK_DYE)
                .requires(ModBlocks.NIGHTSHADE_BUSH.get())
                .unlockedBy("has_nightshade_bush",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.NIGHTSHADE_BUSH.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "black_dye_from_nightshade"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ORANGE_DYE)
                .requires(ModBlocks.DAHLIA.get())
                .unlockedBy("has_dahlia",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.DAHLIA.get()).build()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia", "orange_dye_from_dahlia"));

        // Reversible Compacting Recipes for Blocks
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModItems.SALT.get(),
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_BLOCK.get(),
                "hexalia:salt", "salt","hexalia:salt_block", "salt");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                .pattern("PP")
                .pattern("PP")
                .define('P', ModItems.CELESTIAL_CRYSTAL.get())
                .unlockedBy("has_celestial_crystal",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CELESTIAL_CRYSTAL.get()).build()))
                .save(recipeOutput);

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
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.EARPLUGS.get())
                .pattern("P P")
                .define('P', Items.LEATHER)
                .unlockedBy("has_leather",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.LEATHER).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.PURIFYING_SAC.get())
                .requires(ModItems.SALT.get())
                .requires(ModItems.LOTUS_BLOSSOM.get())
                .requires(Items.LEATHER)
                .unlockedBy("has_salt",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SALT.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.FROST_SAC.get())
                .requires(Items.SNOWBALL)
                .requires(ModItems.CHILLBERRIES.get())
                .requires(Items.LEATHER)
                .unlockedBy("has_chillberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CHILLBERRIES.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.FOUL_SAC.get())
                .requires(Items.SPIDER_EYE)
                .requires(ModBlocks.WITCHWEED.get())
                .requires(Items.LEATHER)
                .unlockedBy("has_witchweed",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WITCHWEED.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.SEARING_SAC.get())
                .requires(ModItems.RABBAGE.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .requires(Items.LEATHER)
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BOGGED_BOOTS.get())
                .pattern("PSP")
                .pattern("A A")
                .define('S', ModItems.SILK_FIBER.get())
                .define('P', ModBlocks.WITCHWEED.get())
                .define('A', Items.DRIED_KELP)
                .unlockedBy("has_ghost_fern",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WITCHWEED.get()).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.THORNBOW.get())
                .pattern(" SF")
                .pattern("REF")
                .pattern(" SF")
                .define('S', Items.STICK)
                .define('E', ModItems.EARTH_NODE.get())
                .define('R', ModItems.RABBAGE)
                .define('F', Items.STRING)
                .unlockedBy("has_earth_node",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.EARTH_NODE).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BRIAR_SICKLE.get())
                .pattern(" SS")
                .pattern("RE ")
                .pattern(" S ")
                .define('S', Items.STICK)
                .define('E', ModItems.EARTH_NODE.get())
                .define('R', ModItems.RABBAGE)
                .unlockedBy("has_earth_node",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.EARTH_NODE).build()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SPIRITROOT_TETHER.get())
                .pattern("ES ")
                .pattern("SP ")
                .pattern("  S")
                .define('S', Items.STRING)
                .define('E', ModItems.EARTH_NODE.get())
                .define('P', Items.ENDER_PEARL)
                .unlockedBy("has_earth_node",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SPIRITROOT_TETHER).build()))
                .save(recipeOutput);

        // Shapeless Recipes for Seeds
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANDRAKE_SEEDS.get())
                .requires(ModItems.MANDRAKE.get())
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SUNFIRE_TOMATO_SEEDS.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(recipeOutput);

        // Shapeless Recipes for Items & Blocks
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHILLBERRY_PIE.get())
                .requires(ModItems.CHILLBERRIES.get())
                .requires(Items.EGG)
                .requires(Items.SUGAR)
                .requires(Items.WHEAT)
                .unlockedBy("has_chillberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.CHILLBERRIES.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SPICY_SANDWICH.get())
                .requires(ModItems.SUNFIRE_TOMATO.get())
                .requires(ModTags.Items.FOODS_BREAD)
                .requires(ModTags.Items.FOODS_COOKED_MEAT)
                .unlockedBy("has_sunfire_tomato",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SUNFIRE_TOMATO.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.MANDRAKE_STEW.get())
                .requires(ModItems.MANDRAKE.get())
                .requires(Items.BOWL)
                .requires(ModTags.Items.FOODS_VEGETABLE)
                .requires(ModTags.Items.FOODS_VEGETABLE)
                .unlockedBy("has_mandrake",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.MANDRAKE.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SILK_FIBER.get(), 2)
                .requires(ModItems.SILKWORM.get())
                .requires(ItemTags.LEAVES)
                .unlockedBy("has_silkworm",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILKWORM.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GALEBERRIES_COOKIE.get(), 4)
                .requires(ModItems.GALEBERRIES.get())
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.SUGAR)
                .unlockedBy("has_galeberries",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.GALEBERRIES.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.CLARITY_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.AIR_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .requires(Items.SUNFLOWER)
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.RAINFALL_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .requires(Blocks.BLUE_ORCHID)
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.TEMPEST_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.FIRE_NODE.get())
                .requires(ModItems.CELESTIAL_CRYSTAL.get())
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.PURITY_IDOL.get(), 1)
                .requires(ModItems.SILK_IDOL.get())
                .requires(ModItems.WATER_NODE.get())
                .requires(ModItems.LOTUS_BLOSSOM.get())
                .requires(ModItems.SALT.get())
                .unlockedBy("has_silk_idol",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.SILK_IDOL.get()).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CELESTIAL_CRYSTAL.get(), 4)
                .requires(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                .unlockedBy("has_celestial_crystal_block",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get()).build()))
                .save(recipeOutput);

        // Shapeless Recipes
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MORTAR_AND_PESTLE.get())
                .requires(Items.BOWL)
                .requires(Items.STONE)
                .unlockedBy("has_bowl",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOWL).build()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.VERDANT_GRIMOIRE.get())
                .requires(Items.BOOK)
                .requires(ModTags.Items.HERBS)
                .unlockedBy("has_book",
                        inventoryTrigger(ItemPredicate.Builder.item().of(Items.BOOK).build()))
                .save(recipeOutput);

        // Mortar and Pestle Recipes
        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModBlocks.SPIRIT_BLOOM.get()),
                        new ItemStack(ModItems.SPIRIT_POWDER.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.SPIRIT_POWDER.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModItems.SIREN_KELP.get()),
                        new ItemStack(ModItems.SIREN_PASTE.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.SIREN_PASTE.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModBlocks.DREAMSHROOM.get()),
                        new ItemStack(ModItems.DREAM_PASTE.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.DREAM_PASTE.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModBlocks.GHOST_FERN.get()),
                        new ItemStack(ModItems.GHOST_POWDER.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.GHOST_POWDER.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModItems.SALTSPROUT.get()),
                        new ItemStack(ModItems.SALT.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.SALT.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ModItems.TREE_RESIN.get()),
                        Ingredient.of(Items.SLIME_BALL),
                        Ingredient.of(ModTags.Items.CRUSHED_HERBS),
                        new ItemStack(ModItems.MUTAVIS.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.MUTAVIS.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(ItemTags.SMALL_FLOWERS),
                        Ingredient.of(Items.HONEYCOMB),
                        Ingredient.of(ModTags.Items.CRUSHED_HERBS),
                        new ItemStack(ModItems.FRAGRANT_NECTAR.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.FRAGRANT_NECTAR.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(Items.POPPY),
                        Ingredient.of(ModItems.RABBAGE),
                        Ingredient.of(Items.AZURE_BLUET),
                        new ItemStack(ModItems.BRAMBLEGUARD_SALVE.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BRAMBLEGUARD_SALVE.getId().getPath() + "_from_mortar"));

        MortarAndPestleRecipeBuilder.mortar(
                        Ingredient.of(Items.CORNFLOWER),
                        Ingredient.of(ModItems.TREE_RESIN),
                        Ingredient.of(Items.OXEYE_DAISY),
                        new ItemStack(ModItems.MENDERS_SALVE.get())
                ).unlockedByItem("has_mortar_and_pestle", ModItems.MORTAR_AND_PESTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.MENDERS_SALVE.getId().getPath() + "_from_mortar"));

        // Mutation Recipes
        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.BLUE_ORCHID),
                        new ItemStack(ModBlocks.SPIRIT_BLOOM.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.SPIRIT_BLOOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.KELP),
                        new ItemStack(ModItems.SIREN_KELP.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.SIREN_KELP.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(ModTags.Items.TULIPS),
                        new ItemStack(ModBlocks.CELESTIAL_BLOOM.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.CELESTIAL_BLOOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Items.BROWN_MUSHROOM),
                        new ItemStack(ModBlocks.DREAMSHROOM.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.DREAMSHROOM.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.FERN),
                        new ItemStack(ModBlocks.GHOST_FERN.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.GHOST_FERN.getId().getPath() + "_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.DIORITE),
                        new ItemStack(Blocks.GRANITE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "granite_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.GRANITE),
                        new ItemStack(Blocks.ANDESITE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "andesite_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.ANDESITE),
                        new ItemStack(Blocks.DIORITE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "diorite_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.DRIPSTONE_BLOCK),
                        new ItemStack(Blocks.TUFF)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "tuff_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.ICE),
                        new ItemStack(Blocks.BLUE_ICE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "blue_ice_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.SAND),
                        new ItemStack(Blocks.RED_SAND)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "red_sand_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.CLAY),
                        new ItemStack(Blocks.MUD)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "mud_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.SNOW_BLOCK),
                        new ItemStack(Blocks.PACKED_ICE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "packed_ice_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.NETHERRACK),
                        new ItemStack(Blocks.BLACKSTONE)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "blackstone_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.DIRT),
                        new ItemStack(Blocks.ROOTED_DIRT)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "rooted_dirt_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.ROOTED_DIRT),
                        new ItemStack(Blocks.PODZOL)
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "podzol_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.LILY_PAD),
                        new ItemStack(ModItems.LOTUS_FLOWER.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "lotus_flower_from_mutation"));

        MutationRecipeBuilder.mutation(
                        Ingredient.of(Blocks.CACTUS),
                        new ItemStack(ModItems.SALTSPROUT.get())
                ).unlockedByItem("has_mutavis", ModItems.MUTAVIS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        "saltsprout_from_mutation"));

        // Ritual Brazier Recipes
        RitualBrazierRecipeBuilder.ritualBrazierRecipe(
                        Ingredient.of(Items.AMETHYST_SHARD),
                        new ItemStack(ModItems.CELESTIAL_CRYSTAL.get())
                ).unlockedByItem("has_amethyst_shard", Items.AMETHYST_SHARD)
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.CELESTIAL_CRYSTAL.getId().getPath() + "_from_brazier"));
        RitualBrazierRecipeBuilder.ritualBrazierRecipe(
                        Ingredient.of(Items.GLOW_BERRIES),
                        new ItemStack(ModItems.GALEBERRIES.get())
                ).unlockedByItem("has_glow_berries", Items.GLOW_BERRIES)
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.GALEBERRIES.getId().getPath() + "_from_brazier"));
        RitualBrazierRecipeBuilder.ritualBrazierRecipe(
                        Ingredient.of(Blocks.AMETHYST_BLOCK),
                        new ItemStack(ModBlocks.CELESTIAL_CRYSTAL_BLOCK.get())
                ).unlockedByItem("has_amethyst_block", Blocks.AMETHYST_BLOCK.asItem())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.CELESTIAL_CRYSTAL_BLOCK.getId().getPath() + "_from_brazier"));

        // Small Cauldron Recipes
        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(ModItems.MANDRAKE.get()),
                        Ingredient.of(ModItems.SPIRIT_POWDER.get()),
                        Ingredient.of(ModItems.TREE_RESIN.get()),
                        Ingredient.of(Items.ROTTEN_FLESH),
                        new ItemStack(ModItems.BREW_OF_BLOODLUST.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_BLOODLUST.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(ModItems.CELESTIAL_CRYSTAL.get()),
                        Ingredient.of(Items.IRON_NUGGET),
                        Ingredient.of(Items.SWEET_BERRIES),
                        Ingredient.of(ModItems.TREE_RESIN.get()),
                        new ItemStack(ModItems.BREW_OF_SPIKESKIN.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_SPIKESKIN.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(Items.SLIME_BALL),
                        Ingredient.of(ModItems.CHILLBERRIES.get()),
                        Ingredient.of(ModItems.TREE_RESIN.get()),
                        Ingredient.of(Items.FEATHER),
                        new ItemStack(ModItems.BREW_OF_SLIMEWALKER.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_SLIMEWALKER.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(ModItems.LOTUS_BLOSSOM.get()),
                        Ingredient.of(Items.ENDER_PEARL),
                        Ingredient.of(ModItems.SPIRIT_POWDER.get()),
                        Ingredient.of(ModItems.GALEBERRIES.get()),
                        new ItemStack(ModItems.BREW_OF_HOMESTEAD.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_HOMESTEAD.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(ModItems.DREAM_PASTE.get()),
                        Ingredient.of(ModItems.SIREN_PASTE.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        Ingredient.of(Items.REDSTONE),
                        new ItemStack(ModItems.BREW_OF_SIPHON.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_SIPHON.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(ModItems.SUNFIRE_TOMATO.get()),
                        Ingredient.of(ModItems.SPIRIT_POWDER.get()),
                        Ingredient.of(Items.GLOW_BERRIES),
                        Ingredient.of(ModBlocks.WITCHWEED.get()),
                        new ItemStack(ModItems.BREW_OF_DAYBLOOM.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_DAYBLOOM.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(Items.SPIDER_EYE),
                        Ingredient.of(ModItems.GHOST_POWDER.get()),
                        Ingredient.of(Items.BLACK_DYE),
                        Ingredient.of(ModItems.LOTUS_BLOSSOM.get()),
                        new ItemStack(ModItems.BREW_OF_ARACHNID_GRACE.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_ARACHNID_GRACE.getId().getPath() + "_from_small_cauldron"));

        SmallCauldronRecipeBuilder.cauldron(
                        Ingredient.of(Items.FEATHER),
                        Ingredient.of(ModItems.GHOST_POWDER.get()),
                        Ingredient.of(ModItems.CHILLBERRIES.get()),
                        Ingredient.of(Items.SCULK),
                        new ItemStack(ModItems.BREW_OF_HOLLOW_SILENCE.get())
                ).unlockedByItem("has_rustic_bottle", ModItems.RUSTIC_BOTTLE.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.BREW_OF_HOLLOW_SILENCE.getId().getPath() + "_from_small_cauldron"));

        // Ritual Table Recipes
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModBlocks.GRIMSHADE.get()))
                .addIngredient(Items.AZURE_BLUET)
                .addIngredient(ModItems.GHOST_POWDER.get())
                .addIngredient(Items.WITHER_ROSE)
                .addIngredient(Items.BONE)
                .addIngredient(Items.BLACK_DYE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.GRIMSHADE.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.RABBAGE_SEEDS.get()))
                .addIngredient(Items.BEETROOT_SEEDS)
                .addIngredient(ModItems.DREAM_PASTE.get())
                .addIngredient(Items.IRON_NUGGET)
                .addIngredient(Items.SWEET_BERRIES)
                .addIngredient(Items.POPPY)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.RABBAGE_SEEDS.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.SAGE_PENDANT.get()))
                .addIngredient(ModItems.CELESTIAL_CRYSTAL.get())
                .addIngredient(ModItems.SPIRIT_POWDER.get())
                .addIngredient(Items.GOLD_NUGGET)
                .addIngredient(Items.BOOK)
                .addIngredient(Items.EXPERIENCE_BOTTLE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.SAGE_PENDANT.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModBlocks.MORPHORA.get()))
                .addIngredient(Items.POPPY)
                .addIngredient(ModItems.DREAM_PASTE.get())
                .addIngredient(ModItems.SPIRIT_POWDER.get())
                .addIngredient(ModItems.EARTH_NODE.get())
                .addIngredient(ModItems.TREE_RESIN.get())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.MORPHORA.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.KELPWEAVE_BLADE.get()))
                .addIngredient(ModItems.ANCIENT_SEED.get())
                .addIngredient(ModItems.WATER_NODE.get())
                .addIngredient(Items.WOODEN_SWORD)
                .addIngredient(Items.KELP)
                .addIngredient(ModItems.SIREN_PASTE.get())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.KELPWEAVE_BLADE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.ROOTSHAPER.get()))
                .addIngredient(ModItems.ANCIENT_SEED.get())
                .addIngredient(ModItems.EARTH_NODE.get())
                .addIngredient(Items.WOODEN_PICKAXE)
                .addIngredient(Items.WOODEN_SHOVEL)
                .addIngredient(ModItems.DREAM_PASTE.get())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.ROOTSHAPER.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.NAUTILITE.get()))
                .addIngredient(Items.KELP)
                .addIngredient(ModItems.SIREN_PASTE.get())
                .addIngredient(ModItems.WATER_NODE.get())
                .addIngredient(Items.NAUTILUS_SHELL)
                .addIngredient(Items.PRISMARINE_CRYSTALS)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.NAUTILITE.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModBlocks.WINDSONG.get()))
                .addIngredient(Items.OXEYE_DAISY)
                .addIngredient(ModItems.AIR_NODE.get())
                .addIngredient(ModItems.GHOST_POWDER.get())
                .addIngredient(Items.FEATHER)
                .addIngredient(Items.PHANTOM_MEMBRANE)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.WINDSONG.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModBlocks.ASTRYLIS.get()))
                .addIngredient(Items.LILY_OF_THE_VALLEY)
                .addIngredient(ModItems.CELESTIAL_CRYSTAL.get())
                .addIngredient(ModItems.EARTH_NODE.get())
                .addIngredient(Items.BONE_MEAL)
                .addIngredient(Items.GLOWSTONE_DUST)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.ASTRYLIS.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.FIRE_NODE.get()))
                .addIngredient(Items.AMETHYST_SHARD)
                .addIngredient(Items.COAL)
                .addIngredient(Items.SUNFLOWER)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.FIRE_NODE.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.AIR_NODE.get()))
                .addIngredient(Items.AMETHYST_SHARD)
                .addIngredient(Items.FEATHER)
                .addIngredient(Items.DANDELION)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.AIR_NODE.getId().getPath() + "_from_ritual_table"));
        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.WATER_NODE.get()))
                .addIngredient(Items.AMETHYST_SHARD)
                .addIngredient(Items.LILY_PAD)
                .addIngredient(Items.INK_SAC)
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.WATER_NODE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModItems.EARTH_NODE.get()))
                .addIngredient(Items.AMETHYST_SHARD)
                .addIngredient(Items.CLAY_BALL)
                .addIngredient(Blocks.BROWN_MUSHROOM.asItem())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModItems.EARTH_NODE.getId().getPath() + "_from_ritual_table"));

        RitualTableRecipeBuilder.ritualTableRecipe(new ItemStack(ModBlocks.LOURDES.get()))
                .addIngredient(Items.BLUE_ORCHID)
                .addIngredient(ModItems.AIR_NODE.get())
                .addIngredient(Items.HONEYCOMB)
                .addIngredient(Items.GLISTERING_MELON_SLICE)
                .addIngredient(ModItems.DREAM_PASTE.get())
                .unlockedByItem("has_hex_focus", ModItems.HEX_FOCUS.get())
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath("hexalia",
                        ModBlocks.LOURDES.getId().getPath() + "_from_ritual_table"));

        // Recipes for Wood-related Blocks
        planksFromLog(recipeOutput, ModBlocks.COTTONWOOD_PLANKS.get(), ModTags.Items.COTTONWOOD_LOGS, 4);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.COTTONWOOD_BUTTON.get())
                .requires(ModBlocks.COTTONWOOD_PLANKS.get())
                .unlockedBy("has_cottonwood_planks",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);

        trapdoorBuilder(ModBlocks.COTTONWOOD_TRAPDOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        doorBuilder(ModBlocks.COTTONWOOD_DOOR.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.COTTONWOOD_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        stairBuilder(ModBlocks.COTTONWOOD_STAIRS.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.COTTONWOOD_SLAB.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        fenceBuilder(ModBlocks.COTTONWOOD_FENCE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        fenceGateBuilder(ModBlocks.COTTONWOOD_FENCE_GATE.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        signBuilder(ModBlocks.COTTONWOOD_SIGN.get(), Ingredient.of(ModBlocks.COTTONWOOD_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.COTTONWOOD_PLANKS.get()).build()))
                .save(recipeOutput);
        hangingSign(recipeOutput, ModItems.COTTONWOOD_HANGING_SIGN.get(), ModBlocks.STRIPPED_COTTONWOOD_LOG.get());

        planksFromLog(recipeOutput, ModBlocks.WILLOW_PLANKS.get(), ModTags.Items.WILLOW_LOGS, 4);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.WILLOW_BUTTON.get())
                .requires(ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy("has_willow_planks",
                        inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);

        trapdoorBuilder(ModBlocks.WILLOW_TRAPDOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        doorBuilder(ModBlocks.WILLOW_DOOR.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.WILLOW_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        stairBuilder(ModBlocks.WILLOW_STAIRS.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        slabBuilder(RecipeCategory.DECORATIONS, ModBlocks.WILLOW_SLAB.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        fenceBuilder(ModBlocks.WILLOW_FENCE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        fenceGateBuilder(ModBlocks.WILLOW_FENCE_GATE.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);

        signBuilder(ModBlocks.WILLOW_SIGN.get(), Ingredient.of(ModBlocks.WILLOW_PLANKS.get()))
                .unlockedBy("has_planks", inventoryTrigger(ItemPredicate.Builder.item().of(ModBlocks.WILLOW_PLANKS.get()).build()))
                .save(recipeOutput);
        hangingSign(recipeOutput, ModItems.WILLOW_HANGING_SIGN.get(), ModBlocks.STRIPPED_WILLOW_LOG.get());
    }
}
