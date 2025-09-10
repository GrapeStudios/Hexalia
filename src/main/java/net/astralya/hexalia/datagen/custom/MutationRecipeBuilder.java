package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutationRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final ItemStack outputStack;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public MutationRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.outputStack = output;
        this.result = output.getItem();
    }

    public static MutationRecipeBuilder mutation(Ingredient input, ItemStack output) {
        return new MutationRecipeBuilder(input, output);
    }

    @Override
    public MutationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return this;
    }

    public MutationRecipeBuilder unlockedByItem(String name, Item item) {
        return this.unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput out, ResourceLocation id) {
        Advancement.Builder adv = out.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(adv::addCriterion);

        MutationRecipe recipe = new MutationRecipe(this.input, this.outputStack);
        out.accept(id, recipe, adv.build(id.withPrefix("recipes/mutation/")));
    }
}