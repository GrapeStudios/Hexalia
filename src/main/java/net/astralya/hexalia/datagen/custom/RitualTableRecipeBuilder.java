package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.RitualTableRecipe;
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
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class RitualTableRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final DefaultedList<Ingredient> ingredients = DefaultedList.of();
    private final ItemStack output;
    private final Item resultItem;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();

    private RitualTableRecipeBuilder(ItemStack output) {
        this.output = output.copy();
        this.resultItem = this.output.getItem();
    }

    public static RitualTableRecipeBuilder ritual(ItemConvertible result, int count) {
        return new RitualTableRecipeBuilder(new ItemStack(result, count));
    }

    public static RitualTableRecipeBuilder ritual(ItemStack output) {
        return new RitualTableRecipeBuilder(output);
    }

    public RitualTableRecipeBuilder tableItem(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder brazierItem(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    public RitualTableRecipeBuilder addIngredient(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder addIngredient(ItemStack stack) {
        return addIngredient(Ingredient.ofStacks(stack));
    }

    @Override
    public RitualTableRecipeBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        advancement.criterion(name, criterion);
        return this;
    }

    @Override
    public RitualTableRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return resultItem;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier saveId) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Ritual Table recipe must have at least 1 ingredient (table item).");
        }
        if (ingredients.size() > 5) {
            throw new IllegalStateException("Too many ingredients for Ritual Table recipe (max 5: 1 table + 4 braziers).");
        }

        Advancement.Builder adv = advancement
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(saveId))
                .rewards(AdvancementRewards.Builder.recipe(saveId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);

        Identifier advId = Identifier.of(saveId.getNamespace(), "recipes/ritual_table/" + saveId.getPath());
        AdvancementEntry advEntry = adv.build(advId);

        RitualTableRecipe recipe = new RitualTableRecipe(ingredients, output);
        exporter.accept(saveId, recipe, advEntry);
    }
}