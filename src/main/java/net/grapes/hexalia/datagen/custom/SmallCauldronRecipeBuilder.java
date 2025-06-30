package net.grapes.hexalia.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SmallCauldronRecipeBuilder implements RecipeBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item result;
    private final Item bottleSlotItem;
    private int brewTime = 175;
    private float experience = 5.0f;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public SmallCauldronRecipeBuilder(List<ItemLike> ingredients, ItemLike bottleSlotItem, ItemLike result) {
        for (ItemLike ingredient : ingredients) {
            this.ingredients.add(Ingredient.of(ingredient));
        }
        this.bottleSlotItem = bottleSlotItem.asItem();
        this.result = result.asItem();
    }

    public SmallCauldronRecipeBuilder brewTime(int brewTime) {
        this.brewTime = brewTime;
        return this;
    }

    public SmallCauldronRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.ingredients, this.bottleSlotItem,
                this.brewTime, this.experience, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/"
                + pRecipeId.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final List<Ingredient> ingredients;
        private final Item bottleSlotItem;
        private final int brewTime;
        private final float experience;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, List<Ingredient> ingredients, Item bottleSlotItem,
                      int brewTime, float experience, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.ingredients = ingredients;
            this.bottleSlotItem = bottleSlotItem;
            this.brewTime = brewTime;
            this.experience = experience;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.addProperty("type", "hexalia:small_cauldron");

            JsonArray jsonIngredients = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                jsonIngredients.add(ingredient.toJson());
            }
            pJson.add("ingredients", jsonIngredients);

            JsonObject jsonBottleSlot = new JsonObject();
            jsonBottleSlot.addProperty("item", ForgeRegistries.ITEMS.getKey(this.bottleSlotItem).toString());
            pJson.add("bottle_slot", jsonBottleSlot);

            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            pJson.add("output", jsonOutput);

            pJson.addProperty("brew_time", this.brewTime);
            pJson.addProperty("experience", this.experience);
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(HexaliaMod.MOD_ID,
                    ForgeRegistries.ITEMS.getKey(this.result).getPath() + "_from_small_cauldron");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return SmallCauldronRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}