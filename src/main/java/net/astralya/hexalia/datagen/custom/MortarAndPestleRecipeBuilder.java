package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
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

import java.util.LinkedHashMap;
import java.util.Map;

public class MortarAndPestleRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack outputStack;
    private final Item result;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

    private MortarAndPestleRecipeBuilder(DefaultedList<Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.outputStack = output.copy();
        this.result = output.getItem();
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    @Override
    public MortarAndPestleRecipeBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        return this;
    }

    public MortarAndPestleRecipeBuilder unlockedByItem(String name, ItemConvertible item) {
        return this.criterion(name, InventoryChangedCriterion.Conditions.items(item));
    }

    @Override
    public Item getOutputItem() {
        return this.result;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier id) {
        Advancement.Builder adv = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);

        this.criteria.forEach(adv::criterion);

        MortarAndPestleRecipe recipe = new MortarAndPestleRecipe(this.inputs, this.outputStack.copy());
        exporter.accept(id, recipe, adv.build(id.withPrefixedPath("recipes/mortar_and_pestle/")));
    }
}