package net.grapes.hexalia.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.recipe.TransmutationRecipe;
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

public class TransmutationRecipeBuilder implements RecipeBuilder {

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item output;
    private final Item input;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public TransmutationRecipeBuilder(List<ItemLike> ingredients, ItemLike input, ItemLike output) {
        for (ItemLike ingredient : ingredients) {
            this.ingredients.add(Ingredient.of(ingredient));
        }
        this.input = input.asItem();
        this.output = output.asItem();
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
        return output;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.output, this.ingredients, this.input,
                this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/"
                + pRecipeId.getPath())));

    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item output;
        private final List<Ingredient> saltItems;
        private final Item input;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item output, List<Ingredient> saltItems, Item input,
                      Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.output = output;
            this.saltItems = saltItems;
            this.input = input;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.addProperty("type", "hexalia:transmutation");

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("item", ForgeRegistries.ITEMS.getKey(this.input).toString());
            pJson.add("input", jsonInput);

            JsonArray jsonSaltItems = new JsonArray();
            for (Ingredient saltItems : saltItems) {
                jsonSaltItems.add(saltItems.toJson());
            }
            pJson.add("salt_items", jsonSaltItems);

            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output).toString());
            pJson.add("output", jsonOutput);
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(HexaliaMod.MOD_ID,
                    ForgeRegistries.ITEMS.getKey(this.output).getPath() + "_transmutation");
        }

        @Override
        public RecipeSerializer<?> getType() {
            return TransmutationRecipe.Serializer.INSTANCE;
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
