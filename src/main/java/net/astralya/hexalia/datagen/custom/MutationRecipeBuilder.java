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
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MutationRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Ingredient input;
    private final ItemStack outputStack;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.create();

    public MutationRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.outputStack = output.copy();
        this.result = this.outputStack.getItem();
    }

    public static MutationRecipeBuilder mutation(Ingredient input, ItemStack output) {
        return new MutationRecipeBuilder(input, output);
    }

    public MutationRecipeBuilder unlockedByItem(String name, ItemConvertible item) {
        return (MutationRecipeBuilder) this.criterion(name,
                net.minecraft.advancement.criterion.InventoryChangedCriterion.Conditions.items(item));
    }

    @Override
    public MutationRecipeBuilder criterion(String name, CriterionConditions conditions) {
        this.advancement.criterion(name, conditions);
        return this;
    }

    @Override
    public MutationRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.result;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier id) {
        this.advancement.parent(new Identifier("recipes/root"))
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                .rewards(AdvancementRewards.Builder.recipe(id));

        Identifier advId = new Identifier(id.getNamespace(), "recipes/mutation/" + id.getPath());

        exporter.accept(new Result(
                id,
                this.input,
                this.outputStack,
                this.advancement,
                advId
        ));
    }

    public static class Result implements RecipeJsonProvider {
        private final Identifier id;
        private final Ingredient input;
        private final ItemStack output;
        private final Advancement.Builder advancement;
        private final Identifier advancementId;

        public Result(Identifier id,
                      Ingredient input,
                      ItemStack output,
                      Advancement.Builder advancement,
                      Identifier advancementId) {
            this.id = id;
            this.input = input;
            this.output = output.copy();
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("type", HexaliaMod.MODID + ":mutation");
            json.add("input", this.input.toJson());

            JsonObject out = new JsonObject();
            out.addProperty("item", Registries.ITEM.getId(this.output.getItem()).toString());
            if (this.output.getCount() != 1) {
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
            return ModRecipes.MUTATION_SERIALIZER;
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