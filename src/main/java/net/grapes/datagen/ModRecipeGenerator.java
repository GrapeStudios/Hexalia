package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;


public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

        // Shaped Recipe for Items & Blocks
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMALL_CAULDRON)
                .pattern("S S")
                .pattern("P P")
                .pattern("SSS")
                .input('P', Items.COPPER_INGOT)
                .input('S', Items.COBBLED_DEEPSLATE)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SMALL_CAULDRON) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DREAMCATCHER)
                .pattern(" S ")
                .pattern("SPS")
                .pattern("ASA")
                .input('P', Items.STRING)
                .input('S', Items.STICK)
                .input('A', Items.FEATHER)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.DREAMCATCHER) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.PARCHMENT)
                .pattern(" S ")
                .pattern(" P ")
                .pattern(" A ")
                .input('S', Items.STICK)
                .input('P', Items.PAPER)
                .input('A', ModBlocks.SPIRIT_BLOOM)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.PARCHMENT) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SALT_LAMP)
                .pattern(" A ")
                .pattern(" P ")
                .pattern(" S ")
                .input('A', Items.TORCH)
                .input('P', ModTags.Items.SALT_BLOCKS)
                .input('S', Items.COPPER_INGOT)
                .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SALT_LAMP) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RUSTIC_BOTTLE, 3)
                .pattern("S S")
                .pattern(" P ")
                .input('S', Items.CLAY_BALL)
                .input('P', Blocks.GLASS)
                .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUSTIC_BOTTLE) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.INFUSED_DIRT, 2)
                .pattern("SP")
                .pattern("PS")
                .input('S', Blocks.DIRT)
                .input('P', ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.SIREN_KELP), conditionsFromItem(ModItems.SIREN_KELP))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.INFUSED_DIRT)  + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.STONE_DAGGER)
                .pattern(" S ")
                .pattern(" P ")
                .input('S', Blocks.COBBLESTONE)
                .input('P', Items.STICK)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.STONE_DAGGER) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RUSTIC_OVEN)
                .pattern("TTT")
                .pattern("SPS")
                .pattern("SSS")
                .input('P', ItemTags.COALS)
                .input('S', Items.COBBLED_DEEPSLATE)
                .input('T', Items.IRON_INGOT)
                .criterion(hasItem(Items.COBBLED_DEEPSLATE), conditionsFromItem(Items.COBBLED_DEEPSLATE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.RUSTIC_OVEN) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RITUAL_TABLE)
                .pattern("STS")
                .pattern(" S ")
                .pattern("SSS")
                .input('S', Items.COBBLED_DEEPSLATE)
                .input('T', Items.MOSS_CARPET)
                .criterion(hasItem(Items.COBBLED_DEEPSLATE), conditionsFromItem(Items.COBBLED_DEEPSLATE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.RITUAL_TABLE) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HEX_FOCUS)
                .pattern("  S")
                .pattern(" P ")
                .pattern("T  ")
                .input('S', Items.AMETHYST_SHARD)
                .input('P', ItemTags.LEAVES)
                .input('T', Items.STICK)
                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.HEX_FOCUS) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BREW_SHELF)
                .pattern("SSS")
                .pattern("SPS")
                .pattern("SSS")
                .input('P', ModItems.RUSTIC_BOTTLE)
                .input('S', ItemTags.PLANKS)
                .criterion(hasItem(ModItems.RUSTIC_BOTTLE), conditionsFromItem(ModItems.RUSTIC_BOTTLE))
                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BREW_SHELF) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CANDLE_SKULL)
                .pattern(" S ")
                .pattern(" T ")
                .pattern("   ")
                .input('S', Items.CANDLE)
                .input('T', Items.SKELETON_SKULL)
                .criterion(hasItem(Items.SKELETON_SKULL), conditionsFromItem(Items.SKELETON_SKULL))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.CANDLE_SKULL) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.WISDOM_GEM)
                .pattern("PSP")
                .pattern("STS")
                .pattern("PSP")
                .input('S', ModItems.SPIRIT_BLOOM_POWDER)
                .input('T', Items.EXPERIENCE_BOTTLE)
                .input('P', Items.AMETHYST_SHARD)
                .criterion(hasItem(Items.EXPERIENCE_BOTTLE), conditionsFromItem(Items.EXPERIENCE_BOTTLE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.WISDOM_GEM) + "_"));

        // Recipes for vanilla items or blocks.
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEATHER, 2)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', ModTags.Items.SALT_DUSTS)
                .input('T', Items.ROTTEN_FLESH)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter, new Identifier(getRecipeName(Items.LEATHER) + "_"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.COBWEB)
                .pattern(" S ")
                .pattern("STS")
                .pattern(" S ")
                .input('S', Items.STRING)
                .input('T', ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter, new Identifier(getRecipeName(Blocks.COBWEB) + "_"));

        // Reversible Compacting Recipes for Blocks
        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.SALT,
                RecipeCategory.MISC, ModBlocks.SALT_BLOCK);

        // Shapeless Recipe for Seeds
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANDRAKE_SEEDS)
                .input(ModItems.MANDRAKE)
                .criterion(hasItem(ModItems.MANDRAKE), conditionsFromItem(ModItems.MANDRAKE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SUNFIRE_TOMATO_SEEDS)
                .input(ModItems.SUNFIRE_TOMATO)
                .criterion(hasItem(ModItems.SUNFIRE_TOMATO), conditionsFromItem(ModItems.SUNFIRE_TOMATO))
                .offerTo(exporter);

        // Shapeless Recipe for Mortar & Pestle, and Mortar & Pestle Resources
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MORTAR_AND_PESTLE, 1)
                .input(Items.BOWL)
                .input(Items.STONE)
                .criterion(hasItem(Items.BOWL), conditionsFromItem(Items.BOWL))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPIRIT_BLOOM_POWDER, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.SPIRIT_BLOOM)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SIREN_KELP_PASTE, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DREAMSHROOM_PASTE, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.DREAMSHROOM)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STRING, 2)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SILK_FIBER)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        // Shapeless Recipe for Items & Blocks
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHILLBERRY_PIE, 1)
                .input(ModItems.CHILLBERRIES)
                .input(Items.SUGAR)
                .input(Items.EGG)
                .input(Items.WHEAT)
                .criterion(hasItem(ModItems.CHILLBERRIES), conditionsFromItem(ModItems.CHILLBERRIES))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.MANDRAKE_STEW, 1)
                .input(ModItems.MANDRAKE)
                .input(Items.BOWL)
                .input(Items.CARROT)
                .input(Items.POTATO)
                .criterion(hasItem(ModItems.MANDRAKE), conditionsFromItem(ModItems.MANDRAKE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.SPICY_SANDWICH, 1)
                .input(Items.BREAD)
                .input(ModItems.SUNFIRE_TOMATO)
                .input(ModTags.Items.COOKED_MEATS)
                .criterion(hasItem(ModItems.SUNFIRE_TOMATO), conditionsFromItem(ModItems.SUNFIRE_TOMATO))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PURIFYING_SALTS, 1)
                .input(ModTags.Items.SALT_DUSTS)
                .input(Items.LEATHER)
                .input(ModTags.Items.CRUSHED_PLANTS)
                .criterion(hasItem(ModItems.SALT), conditionsFromItem(ModItems.SALT))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.YELLOW_DYE)
                .input(ModBlocks.HENBANE)
                .criterion(hasItem(ModBlocks.HENBANE), conditionsFromItem(ModBlocks.HENBANE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HEXBOOK, 1)
                .input(Items.BOOK)
                .input(ModTags.Items.CRUSHED_PLANTS)
                .criterion(hasItem(Items.BOOK), conditionsFromItem(Items.BOOK))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILK_FIBER, 2)
                .input(ItemTags.LEAVES)
                .input(ModItems.SILKWORM)
                .criterion(hasItem(ModItems.SILK_FIBER), conditionsFromItem(ModItems.SILK_FIBER))
                .offerTo(exporter);

        // Recipes for Wooden Blocks
        createTrapdoorRecipe(ModBlocks.COTTONWOOD_TRAPDOOR, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createDoorRecipe(ModBlocks.COTTONWOOD_DOOR, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createPressurePlateRecipe(RecipeCategory.REDSTONE, ModBlocks.COTTONWOOD_PRESSURE_PLATE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createStairsRecipe(ModBlocks.COTTONWOOD_STAIRS, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createSlabRecipe(RecipeCategory.DECORATIONS, ModBlocks.COTTONWOOD_SLAB, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createFenceRecipe(ModBlocks.COTTONWOOD_FENCE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        createFenceGateRecipe(ModBlocks.COTTONWOOD_FENCE_GATE, Ingredient.ofItems(ModBlocks.COTTONWOOD_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.COTTONWOOD_PLANKS))
                .offerTo(exporter);
        offerPlanksRecipe(exporter, ModBlocks.COTTONWOOD_PLANKS, ModTags.Items.COTTONWOOD_LOGS, 4);
        offerSingleOutputShapelessRecipe(exporter, ModBlocks.COTTONWOOD_BUTTON, ModBlocks.COTTONWOOD_PLANKS, "wooden_button");
        offerBoatRecipe(exporter, ModItems.COTTONWOOD_BOAT, ModBlocks.COTTONWOOD_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.COTTONWOOD_CHEST_BOAT, ModItems.COTTONWOOD_BOAT);
        
        createTrapdoorRecipe(ModBlocks.WILLOW_TRAPDOOR, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createDoorRecipe(ModBlocks.WILLOW_DOOR, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createPressurePlateRecipe(RecipeCategory.REDSTONE, ModBlocks.WILLOW_PRESSURE_PLATE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createStairsRecipe(ModBlocks.WILLOW_STAIRS, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createSlabRecipe(RecipeCategory.DECORATIONS, ModBlocks.WILLOW_SLAB, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createFenceRecipe(ModBlocks.WILLOW_FENCE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        createFenceGateRecipe(ModBlocks.WILLOW_FENCE_GATE, Ingredient.ofItems(ModBlocks.WILLOW_PLANKS))
                .criterion("has_planks", InventoryChangedCriterion.Conditions.items(ModBlocks.WILLOW_PLANKS))
                .offerTo(exporter);
        offerPlanksRecipe(exporter, ModBlocks.WILLOW_PLANKS, ModTags.Items.WILLOW_LOGS, 4);
        offerSingleOutputShapelessRecipe(exporter, ModBlocks.WILLOW_BUTTON, ModBlocks.WILLOW_PLANKS, "wooden_button");
        offerBoatRecipe(exporter, ModItems.WILLOW_BOAT, ModBlocks.WILLOW_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.WILLOW_CHEST_BOAT, ModItems.WILLOW_BOAT);
    }
}