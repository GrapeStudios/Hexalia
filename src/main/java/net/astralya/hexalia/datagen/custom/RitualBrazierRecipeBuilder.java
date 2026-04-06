package net.astralya.hexalia.datagen.custom;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RitualBrazierRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Ingredient input;
    private final ItemStack output;
    private final Item resultItem;
    private final Advancement.Builder advancement = Advancement.Builder.createUntelemetered();

    public RitualBrazierRecipeBuilder(ItemConvertible input, ItemStack output) {
        this.input = Ingredient.ofItems(input);
        this.output = output.copy();
        this.resultItem = output.getItem();
    }

    public static RitualBrazierRecipeBuilder ritual(ItemConvertible input, ItemConvertible result, int count) {
        return new RitualBrazierRecipeBuilder(input, new ItemStack(result, count));
    }

    public static RitualBrazierRecipeBuilder ritual(ItemConvertible input, ItemStack output) {
        return new RitualBrazierRecipeBuilder(input, output);
    }

    @Override
    public RitualBrazierRecipeBuilder criterion(String name, net.minecraft.advancement.AdvancementCriterion<?> criterion) {
        advancement.criterion(name, criterion);
        return this;
    }

    @Override
    public RitualBrazierRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return resultItem;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        Advancement.Builder adv = Advancement.Builder.createUntelemetered()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);

        Identifier advId = Identifier.of(
                recipeId.getNamespace(),
                "recipes/ritual_brazier/" + recipeId.getPath()
        );
        AdvancementEntry advEntry = adv.build(advId);

        RitualBrazierRecipe recipe = new RitualBrazierRecipe(input, output);

        Identifier saveId = Identifier.of(
                HexaliaMod.MODID,
                Registries.ITEM.getId(output.getItem()).getPath() + "_from_ritual_brazier"
        );

        exporter.accept(saveId, recipe, advEntry);
    }

}