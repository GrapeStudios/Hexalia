package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.ModTags;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;


public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

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

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPIRIT_BLOOM_DUST, 1)
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

        // Shapeless Recipe for Food Items
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHILLBERRY_CUPCAKE, 1)
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
                .input(ModTags.Items.COOKED_MEAT)
                .criterion(hasItem(ModItems.MANDRAKE), conditionsFromItem(ModItems.MANDRAKE))
                .offerTo(exporter);
    }
}