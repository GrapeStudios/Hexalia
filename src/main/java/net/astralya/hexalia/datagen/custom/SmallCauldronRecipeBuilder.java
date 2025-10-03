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

public class SmallCauldronRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final DefaultedList<Ingredient> ingredients = DefaultedList.of();
    private Ingredient bottle = Ingredient.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private float experience = 0.0f;
    private int brewTime = 200;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    public static SmallCauldronRecipeBuilder smallCauldron() {
        return new SmallCauldronRecipeBuilder();
    }

    public SmallCauldronRecipeBuilder addIngredient(ItemConvertible item) {
        this.ingredients.add(Ingredient.ofItems(item));
        return this;
    }

    public SmallCauldronRecipeBuilder bottle(ItemConvertible item) {
        this.bottle = Ingredient.ofItems(item);
        return this;
    }

    public SmallCauldronRecipeBuilder result(ItemConvertible item, int count) {
        this.result = new ItemStack(item, count);
        return this;
    }

    public SmallCauldronRecipeBuilder experience(float xp) {
        this.experience = xp;
        return this;
    }

    public SmallCauldronRecipeBuilder brewTime(int ticks) {
        this.brewTime = ticks;
        return this;
    }

    @Override
    public SmallCauldronRecipeBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    @Override
    public SmallCauldronRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.result.getItem();
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("Small Cauldron recipe needs at least 1 ingredient.");
        }
        if (this.bottle == Ingredient.EMPTY) {
            throw new IllegalStateException("Small Cauldron recipe requires a bottle ingredient.");
        }
        if (this.result.isEmpty()) {
            throw new IllegalStateException("Small Cauldron recipe requires a result item.");
        }

        this.advancement.parent(new Identifier("recipes/root"))
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId));

        Identifier advId = new Identifier(recipeId.getNamespace(), "recipes/small_cauldron/" + recipeId.getPath());

        exporter.accept(new JsonBuilder(
                recipeId,
                this.ingredients,
                this.bottle,
                this.result,
                this.experience,
                this.brewTime,
                this.advancement,
                advId
        ));
    }

    public static class JsonBuilder implements RecipeJsonProvider {
        private final Identifier id;
        private final DefaultedList<Ingredient> ingredients;
        private final Ingredient bottle;
        private final ItemStack result;
        private final float experience;
        private final int brewTime;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public JsonBuilder(Identifier id,
                           DefaultedList<Ingredient> ingredients,
                           Ingredient bottle,
                           ItemStack result,
                           float experience,
                           int brewTime,
                           Advancement.Builder advancement,
                           Identifier advancementId) {
            this.id = id;
            this.ingredients = ingredients;
            this.bottle = bottle;
            this.result = result.copy();
            this.experience = experience;
            this.brewTime = brewTime;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            JsonArray ingArr = new JsonArray();
            for (Ingredient ing : this.ingredients) {
                ingArr.add(ing.toJson());
            }
            json.add("ingredients", ingArr);

            json.add("bottle_slot", this.bottle.toJson());

            JsonObject outputObj = new JsonObject();
            outputObj.addProperty("item", Registries.ITEM.getId(this.result.getItem()).toString());
            if (this.result.getCount() > 1) {
                outputObj.addProperty("count", this.result.getCount());
            }
            json.add("output", outputObj);

            json.addProperty("experience", this.experience);
            json.addProperty("brew_time", this.brewTime);
        }

        @Override
        public Identifier getRecipeId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return ModRecipes.SMALL_CAULDRON_SERIALIZER;
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