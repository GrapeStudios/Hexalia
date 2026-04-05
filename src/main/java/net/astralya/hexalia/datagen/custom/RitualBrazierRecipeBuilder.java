package net.astralya.hexalia.datagen.custom;

import com.google.gson.JsonObject;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.ModRecipes;
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

import java.util.function.Consumer;

public class RitualBrazierRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final Ingredient input;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    public RitualBrazierRecipeBuilder(ItemConvertible input, ItemConvertible result) {
        this.input = Ingredient.ofItems(input);
        this.result = result.asItem();
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
        return result;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.advancement.parent(new Identifier("recipes/root"))
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId));

        exporter.accept(new JsonBuilder(recipeId, this.result, this.input,
                this.advancement, new Identifier(recipeId.getNamespace(), "recipes/" + recipeId.getPath())));
    }

    public static class JsonBuilder implements RecipeJsonProvider {
        private final Identifier id;
        private final Item result;
        private final Ingredient input;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public JsonBuilder(Identifier id, Item result, Ingredient input,
                           Advancement.Builder advancement, Identifier advancementId) {
            this.id = id;
            this.result = result;
            this.input = input;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("type", "hexalia:ritual_brazier");

            json.add("input", input.toJson());

            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("item", Registries.ITEM.getId(this.result).toString());
            json.add("output", outputJson);
        }

        @Override
        public Identifier getRecipeId() {
            return new Identifier(HexaliaMod.MODID,
                    Registries.ITEM.getId(this.result).getPath() + "_from_ritual");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return ModRecipes.RITUAL_BRAZIER_SERIALIZER;
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