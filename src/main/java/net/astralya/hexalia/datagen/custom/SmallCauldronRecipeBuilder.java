package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmallCauldronRecipeBuilder {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack outputStack;
    private final float experience;
    private final int brewTime;

    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    private SmallCauldronRecipeBuilder(DefaultedList<Ingredient> inputs, ItemStack outputStack, float experience, int brewTime) {
        this.inputs = inputs;
        this.outputStack = outputStack;
        this.experience = experience;
        this.brewTime = brewTime;
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        inputs.add(input4);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public SmallCauldronRecipeBuilder experience(float experience) {
        return new SmallCauldronRecipeBuilder(this.inputs, this.outputStack, experience, this.brewTime).copyCriteriaFrom(this);
    }

    public SmallCauldronRecipeBuilder brewTime(int brewTime) {
        return new SmallCauldronRecipeBuilder(this.inputs, this.outputStack, this.experience, brewTime).copyCriteriaFrom(this);
    }

    private SmallCauldronRecipeBuilder copyCriteriaFrom(SmallCauldronRecipeBuilder other) {
        this.criteria.putAll(other.criteria);
        return this;
    }

    public SmallCauldronRecipeBuilder unlockedBy(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public Item getResult() {
        return this.outputStack.getItem();
    }

    public void save(RecipeExporter exporter, Identifier id) {
        Advancement.Builder adv = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);

        this.criteria.forEach(adv::criterion);

        SmallCauldronRecipe recipe = new SmallCauldronRecipe(this.inputs, this.outputStack, this.experience, this.brewTime);
        exporter.accept(id, recipe, adv.build(id.withPrefixedPath("recipes/small_cauldron/")));
    }
}