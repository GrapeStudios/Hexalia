package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.NaturesRitualRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class NaturesRitualRecipeBuilder implements RecipeBuilder {
  private final RecipeCategory category;
  private final NonNullList<Ingredient> ingredients = NonNullList.create();
  private final Item result;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  private String group;

  private NaturesRitualRecipeBuilder(
      RecipeCategory category, Ingredient centerIngredient, ItemLike result) {
    this.category = category;
    this.result = result.asItem();
    ingredients.add(centerIngredient);
  }

  public static NaturesRitualRecipeBuilder ritual(
      RecipeCategory category, Ingredient centerIngredient, ItemLike result) {
    return new NaturesRitualRecipeBuilder(category, centerIngredient, result);
  }

  public NaturesRitualRecipeBuilder requiresBrazierIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
    return this;
  }

  @Override
  public NaturesRitualRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    criteria.put(name, criterion);
    return this;
  }

  @Override
  public NaturesRitualRecipeBuilder group(String recipeGroup) {
    group = recipeGroup;
    return this;
  }

  @Override
  public Item getResult() {
    return result;
  }

  @Override
  public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
    ensureValid(recipeId);

    Advancement.Builder advancement =
        recipeOutput
            .advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId));
    criteria.forEach(advancement::addCriterion);

    NonNullList<Ingredient> recipeIngredients = NonNullList.create();
    recipeIngredients.addAll(ingredients);
    recipeOutput.accept(
        recipeId,
        new NaturesRitualRecipe(recipeIngredients, new ItemStack(result)),
        advancement.build(recipeId.withPrefix("recipes/" + category.getFolderName() + "/")));
  }

  private void ensureValid(ResourceLocation recipeId) {
    if (criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
    if (ingredients.isEmpty()) {
      throw new IllegalStateException("Nature's Ritual recipe requires a center ingredient");
    }
  }
}
