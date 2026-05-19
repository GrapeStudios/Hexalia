package net.astralya.hexalia.neoforge.datagen.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class MortarAndPestleRecipeBuilder implements RecipeBuilder {
  private final NonNullList<Ingredient> inputs = NonNullList.create();
  private final ItemStack output;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  private MortarAndPestleRecipeBuilder(ItemStack output) {
    this.output = output;
  }

  public static MortarAndPestleRecipeBuilder mortar(Ingredient input, ItemStack output) {
    MortarAndPestleRecipeBuilder builder = new MortarAndPestleRecipeBuilder(output);
    builder.inputs.add(input);
    return builder;
  }

  public MortarAndPestleRecipeBuilder requires(Ingredient input) {
    inputs.add(input);
    return this;
  }

  @Override
  public MortarAndPestleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    criteria.put(name, criterion);
    return this;
  }

  @Override
  public RecipeBuilder group(@Nullable String groupName) {
    return this;
  }

  @Override
  public Item getResult() {
    return output.getItem();
  }

  @Override
  public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
    if (criteria.isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + recipeId);
    }
    if (inputs.isEmpty() || inputs.size() > 3) {
      throw new IllegalStateException("Mortar and Pestle recipes require 1 to 3 ingredients");
    }

    Advancement.Builder advancement =
        recipeOutput
            .advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .requirements(AdvancementRequirements.Strategy.OR);
    criteria.forEach(advancement::addCriterion);

    NonNullList<Ingredient> recipeInputs = NonNullList.create();
    recipeInputs.addAll(inputs);
    recipeOutput.accept(
        recipeId,
        new MortarAndPestleRecipe(recipeInputs, output),
        advancement.build(recipeId.withPrefix("recipes/mortar_and_pestle/")));
  }
}
