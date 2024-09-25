package net.grapes.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
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

public class SmallCauldronRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item result;
    private final Item bottleSlotItem;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    public SmallCauldronRecipeBuilder(List<ItemConvertible> ingredients, ItemConvertible bottleSlotItem, ItemConvertible result) {
        for (ItemConvertible ingredient : ingredients) {
            this.ingredients.add(Ingredient.ofItems(ingredient));
        }
        this.bottleSlotItem = bottleSlotItem.asItem();
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

        exporter.accept(new JsonBuilder(recipeId, this.result, this.ingredients, this.bottleSlotItem,
                this.advancement, new Identifier(recipeId.getNamespace(), "recipes/" + recipeId.getPath())));
    }

    public static class JsonBuilder implements RecipeJsonProvider {
        private final Identifier id;
        private final Item result;
        private final List<Ingredient> ingredients;
        private final Item bottleSlotItem;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public JsonBuilder(Identifier id, Item result, List<Ingredient> ingredients, Item bottleSlotItem,
                           Advancement.Builder advancement, Identifier advancementId) {
            this.id = id;
            this.result = result;
            this.ingredients = ingredients;
            this.bottleSlotItem = bottleSlotItem;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("type", "hexalia:small_cauldron");

            JsonArray jsonIngredients = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                jsonIngredients.add(ingredient.toJson());
            }
            json.add("ingredients", jsonIngredients);

            JsonObject jsonBottleSlot = new JsonObject();
            jsonBottleSlot.addProperty("item", Registries.ITEM.getId(this.bottleSlotItem).toString());
            json.add("bottle_slot", jsonBottleSlot);

            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("item", Registries.ITEM.getId(this.result).toString());
            json.add("output", jsonOutput);
        }

        @Override
        public Identifier getRecipeId() {
            return new Identifier(HexaliaMod.MOD_ID,
                    Registries.ITEM.getId(this.result).getPath() + "_from_small_cauldron");
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return SmallCauldronRecipe.Serializer.INSTANCE;
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
