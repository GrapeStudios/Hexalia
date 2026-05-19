package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class CelestialInfusionRecipeBuilder implements RecipeBuilder {
  private final RecipeCategory category;
  private final Ingredient input;
  private final Item result;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  private String group;

  private CelestialInfusionRecipeBuilder(
      RecipeCategory category, Ingredient input, ItemLike result) {
    this.category = category;
    this.input = input;
    this.result = result.asItem();
  }

  public static CelestialInfusionRecipeBuilder infusion(
      RecipeCategory category, Ingredient input, ItemLike result) {
    return new CelestialInfusionRecipeBuilder(category, input, result);
  }

  @Override
  public CelestialInfusionRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    criteria.put(name, criterion);
    return this;
  }

  @Override
  public CelestialInfusionRecipeBuilder group(String recipeGroup) {
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

    recipeOutput.accept(
        recipeId,
        new CelestialInfusionRecipe(input, new ItemStack(result)),
        advancement.build(recipeId.withPrefix("recipes/" + category.getFolderName() + "/")));
  }

  private void ensureValid(ResourceLocation recipeId) {
    if (criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
  }
}
