package net.grapes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;


public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerSingleOutputShapelessRecipe(exporter, ModItems.SPIRIT_BLOOM_PETAL,
                ModBlocks.SPIRIT_BLOOM, "component");

        offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, ModItems.SALT,
                RecipeCategory.MISC, ModBlocks.SALT_BLOCK);

        // Shapeless Recipe for Mortar & Pestle

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPIRIT_BLOOM_DUST, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SPIRIT_BLOOM_PETAL)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SIREN_KELP_PASTE, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModItems.SIREN_KELP)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DREAMSHROOM_DUST, 1)
                .input(ModItems.MORTAR_AND_PESTLE)
                .input(ModBlocks.DREAMSHROOM)
                .criterion(hasItem(ModItems.MORTAR_AND_PESTLE), conditionsFromItem(ModItems.MORTAR_AND_PESTLE))
                .offerTo(exporter);
    }
}