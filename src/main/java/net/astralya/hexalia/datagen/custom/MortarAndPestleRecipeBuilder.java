package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MortarAndPestleRecipeBuilder implements RecipeBuilder {

    private final NonNullList<Ingredient> inputs;
    private final ItemStack outputStack;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private MortarAndPestleRecipeBuilder(NonNullList<Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.outputStack = output;
        this.result = output.getItem();
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    @Override
    public MortarAndPestleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return this;
    }

    public MortarAndPestleRecipeBuilder unlockedByItem(String name, Item item) {
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

        MortarAndPestleRecipe recipe = new MortarAndPestleRecipe(this.inputs, this.outputStack);
        out.accept(id, recipe, adv.build(id.withPrefix("recipes/mortar_and_pestle/")));
    }
}