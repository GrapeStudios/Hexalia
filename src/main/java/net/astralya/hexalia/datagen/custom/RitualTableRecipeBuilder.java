package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;


public class RitualTableRecipeBuilder implements RecipeBuilder {
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final ItemStack output;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public RitualTableRecipeBuilder(ItemStack output) {
        this.output = output;
        this.result = output.getItem();
    }

    public static RitualTableRecipeBuilder ritualTableRecipe(ItemStack output) {
        return new RitualTableRecipeBuilder(output);
    }

    public RitualTableRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public RitualTableRecipeBuilder addIngredient(Item item) {
        return this.addIngredient(Ingredient.of(item));
    }

    public RitualTableRecipeBuilder addIngredient(ItemStack stack) {
        return this.addIngredient(Ingredient.of(stack));
    }

    @Override
    public RitualTableRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public RitualTableRecipeBuilder unlockedByItem(String name, Item item) {
        return this.unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    public RitualTableRecipeBuilder unlockedByItems(String name, Item... items) {
        return this.unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(items));
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("No ingredients defined for Ritual Table recipe");
        }
        if (this.ingredients.size() > RitualTableRecipe.INPUT_SLOTS + 1) { // 1 main + 4 braziers max
            throw new IllegalStateException("Too many ingredients for Ritual Table recipe (max " + (RitualTableRecipe.INPUT_SLOTS + 1) + ")");
        }

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        RitualTableRecipe recipe = new RitualTableRecipe(this.ingredients, this.output);
        output.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/ritual_table/")));
    }
}