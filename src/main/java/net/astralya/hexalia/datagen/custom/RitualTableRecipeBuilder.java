package net.astralya.hexalia.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.astralya.hexalia.recipe.ModRecipes;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RitualTableRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final DefaultedList<Ingredient> ingredients = DefaultedList.of();
    private final ItemStack output;
    private final Item resultItem;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    private RitualTableRecipeBuilder(ItemStack output) {
        this.output = output.copy();
        this.resultItem = this.output.getItem();
    }

    public static RitualTableRecipeBuilder ritual(ItemConvertible result, int count) {
        return new RitualTableRecipeBuilder(new ItemStack(result, count));
    }

    public static RitualTableRecipeBuilder ritual(ItemStack output) {
        return new RitualTableRecipeBuilder(output);
    }

    public RitualTableRecipeBuilder tableItem(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder brazierItem(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public RitualTableRecipeBuilder addIngredient(ItemConvertible item) {
        return addIngredient(Ingredient.ofItems(item));
    }

    public RitualTableRecipeBuilder addIngredient(ItemStack stack) {
        return addIngredient(Ingredient.ofStacks(stack));
    }

    @Override
    public RitualTableRecipeBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    @Override
    public RitualTableRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.resultItem;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("Ritual Table recipe must have at least 1 ingredient (table item).");
        }
        if (this.ingredients.size() > 5) {
            throw new IllegalStateException("Too many ingredients for Ritual Table recipe (max 5: 1 table + 4 braziers).");
        }

        this.advancement.parent(new Identifier("recipes/root"))
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId));

        Identifier advId = new Identifier(recipeId.getNamespace(), "recipes/ritual_table/" + recipeId.getPath());

        exporter.accept(new JsonBuilder(
                recipeId,
                this.output,
                this.ingredients,
                this.advancement,
                advId
        ));
    }

    public static class JsonBuilder implements RecipeJsonProvider {
        private final Identifier id;
        private final ItemStack output;
        private final DefaultedList<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public JsonBuilder(Identifier id, ItemStack output, DefaultedList<Ingredient> ingredients,
                           Advancement.Builder advancement, Identifier advancementId) {
            this.id = id;
            this.output = output.copy();
            this.ingredients = ingredients;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("type", "hexalia:ritual_table");

            JsonArray ing = new JsonArray();
            for (Ingredient i : this.ingredients) {
                ing.add(i.toJson());
            }
            json.add("ingredients", ing);

            JsonObject out = new JsonObject();
            out.addProperty("item", Registries.ITEM.getId(this.output.getItem()).toString());
            if (this.output.getCount() > 1) {
                out.addProperty("count", this.output.getCount());
            }
            json.add("output", out);
        }

        @Override
        public Identifier getRecipeId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return ModRecipes.RITUAL_TABLE_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return this.advancement.toJson();
        }

        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}