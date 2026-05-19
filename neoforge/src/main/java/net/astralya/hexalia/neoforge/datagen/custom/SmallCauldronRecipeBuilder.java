package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
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

public final class SmallCauldronRecipeBuilder implements RecipeBuilder {
  private final RecipeCategory category;
  private final NonNullList<Ingredient> ingredients = NonNullList.create();
  private final Item result;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  private String group;
  private float experience;
  private int brewTime = 200;

  private SmallCauldronRecipeBuilder(
      RecipeCategory category, Ingredient firstIngredient, ItemLike result) {
    this.category = category;
    this.result = result.asItem();
    ingredients.add(firstIngredient);
  }

  public static SmallCauldronRecipeBuilder brew(
      RecipeCategory category, Ingredient firstIngredient, ItemLike result) {
    return new SmallCauldronRecipeBuilder(category, firstIngredient, result);
  }

  public SmallCauldronRecipeBuilder requiresIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
    return this;
  }

  public SmallCauldronRecipeBuilder experience(float recipeExperience) {
    experience = recipeExperience;
    return this;
  }

  public SmallCauldronRecipeBuilder brewTime(int ticks) {
    brewTime = ticks;
    return this;
  }

  @Override
  public SmallCauldronRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    criteria.put(name, criterion);
    return this;
  }

  @Override
  public SmallCauldronRecipeBuilder group(String recipeGroup) {
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
        new SmallCauldronRecipe(recipeIngredients, new ItemStack(result), experience, brewTime),
        advancement.build(recipeId.withPrefix("recipes/" + category.getFolderName() + "/")));
  }

  private void ensureValid(ResourceLocation recipeId) {
    if (criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
    if (ingredients.isEmpty()) {
      throw new IllegalStateException("Small Cauldron recipe requires at least one ingredient");
    }
  }
}
