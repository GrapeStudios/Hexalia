package net.grapes.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TransmutationRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item output;
    private final Item input;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    public TransmutationRecipeBuilder(List<ItemConvertible> ingredients, ItemConvertible input, ItemConvertible output) {
        for (ItemConvertible ingredient : ingredients) {
            this.ingredients.add(Ingredient.ofItems(ingredient));
        }
        this.input = input.asItem();
        this.output = output.asItem();
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return output;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.advancement.parent(new Identifier("recipes/root"))
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId));

        exporter.accept(new JsonBuilder(recipeId, this.output, this.ingredients, this.input,
                this.advancement, new Identifier(recipeId.getNamespace(), "recipes/" + recipeId.getPath())));
    }

    public static class JsonBuilder implements RecipeJsonProvider {
        private final Identifier id;
        private final Item output;
        private final List<Ingredient> saltItems;
        private final Item input;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public JsonBuilder(Identifier id, Item output, List<Ingredient> saltItems, Item input,
                           Advancement.Builder advancement, Identifier advancementId) {
            this.id = id;
            this.output = output;
            this.saltItems = saltItems;
            this.input = input;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("type", "hexalia:transmutation");

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("item", Registries.ITEM.getId(this.input).toString());
            json.add("input", jsonInput);

            JsonArray jsonSaltItems = new JsonArray();
            for (Ingredient saltItems : saltItems) {
                jsonSaltItems.add(saltItems.toJson());
            }
            json.add("salt_items", jsonSaltItems);

            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("item", Registries.ITEM.getId(this.output).toString());
            json.add("output", jsonOutput);
        }

        @Override
        public Identifier getRecipeId() {
            return new Identifier(HexaliaMod.MOD_ID,
                    Registries.ITEM.getId(this.output).getPath() + "_transmutation");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return TransmutationRecipe.Serializer.INSTANCE;
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
