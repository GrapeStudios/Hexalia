package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
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

public class SmallCauldronRecipeBuilder implements RecipeBuilder {

    private final NonNullList<Ingredient> inputs;
    private final ItemStack outputStack;
    private final float experience;
    private final int brewTime;
    private final Item result;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private SmallCauldronRecipeBuilder(NonNullList<Ingredient> inputs, ItemStack outputStack, float experience, int brewTime) {
        this.inputs = inputs;
        this.outputStack = outputStack;
        this.experience = experience;
        this.brewTime = brewTime;
        this.result = outputStack.getItem();
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        return new SmallCauldronRecipeBuilder(inputs, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
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

    @Override
    public SmallCauldronRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public SmallCauldronRecipeBuilder unlockedByItem(String name, Item item) {
        return this.unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
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

        SmallCauldronRecipe recipe = new SmallCauldronRecipe(this.inputs, this.outputStack, this.experience, this.brewTime);
        out.accept(id, recipe, adv.build(id.withPrefix("recipes/small_cauldron/")));
    }
}
