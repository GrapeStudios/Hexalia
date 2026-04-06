package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutationRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Ingredient input;
    private final ItemStack output;
    private final Item resultItem;

    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    public MutationRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output.copy();
        this.resultItem = output.getItem();
    }

    public static MutationRecipeBuilder mutation(Ingredient input, ItemStack output) {
        return new MutationRecipeBuilder(input, output);
    }

    public static MutationRecipeBuilder mutation(ItemConvertible input, ItemStack output) {
        return new MutationRecipeBuilder(Ingredient.ofItems(input), output);
    }

    @Override
    public MutationRecipeBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public MutationRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return resultItem;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        Advancement.Builder adv = Advancement.Builder.createUntelemetered()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);

        this.criteria.forEach(adv::criterion);

        Identifier advId = Identifier.of(
                recipeId.getNamespace(),
                "recipes/mutation/" + recipeId.getPath()
        );
        AdvancementEntry advEntry = adv.build(advId);

        MutationRecipe recipe = new MutationRecipe(input, output);

        exporter.accept(recipeId, recipe, advEntry);
    }
}