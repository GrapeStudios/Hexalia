package net.astralya.hexalia.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.astralya.hexalia.recipe.ModRecipes;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final ItemStack outputStack;
    private final float experience;
    private final int brewTime;
    private final Item result;
    private final Ingredient[] inputs;
    private final Map<String, CriterionConditions> criteria = new LinkedHashMap<>();

    private SmallCauldronRecipeBuilder(Ingredient[] inputs, ItemStack outputStack, float experience, int brewTime) {
        this.inputs = inputs;
        this.outputStack = outputStack.copy();
        this.experience = experience;
        this.brewTime = brewTime;
        this.result = outputStack.getItem();
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input, ItemStack output) {
        return new SmallCauldronRecipeBuilder(new Ingredient[]{input}, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, ItemStack output) {
        return new SmallCauldronRecipeBuilder(new Ingredient[]{input1, input2}, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        return new SmallCauldronRecipeBuilder(new Ingredient[]{input1, input2, input3}, output, 0.0F, 200);
    }

    public static SmallCauldronRecipeBuilder cauldron(Ingredient input1, Ingredient input2, Ingredient input3, Ingredient input4, ItemStack output) {
        return new SmallCauldronRecipeBuilder(new Ingredient[]{input1, input2, input3, input4}, output, 0.0F, 200);
    }

    public SmallCauldronRecipeBuilder experience(float experience) {
        return new SmallCauldronRecipeBuilder(this.inputs, this.outputStack, experience, this.brewTime).copyCriteriaFrom(this);
    }

    public SmallCauldronRecipeBuilder brewTime(int brewTime) {
        return new SmallCauldronRecipeBuilder(this.inputs, this.outputStack, this.experience, brewTime).copyCriteriaFrom(this);
    }

    private SmallCauldronRecipeBuilder copyCriteriaFrom(SmallCauldronRecipeBuilder other) {
        this.criteria.putAll(other.criteria);
        return this;
    }

    @Override
    public SmallCauldronRecipeBuilder criterion(String name, CriterionConditions criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public SmallCauldronRecipeBuilder unlockedByItem(String name, ItemConvertible item) {
        return this.criterion(name, InventoryChangedCriterion.Conditions.items(item));
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.result;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier id) {
        Identifier advancementId = id.withPrefixedPath("recipes/small_cauldron/");
        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .parent(ROOT)
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .criteriaMerger(CriterionMerger.OR);

        this.criteria.forEach(advancementBuilder::criterion);

        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
                JsonArray ingredientsArray = new JsonArray();
                for (Ingredient input : inputs) {
                    ingredientsArray.add(input.toJson());
                }
                json.add("ingredients", ingredientsArray);

                JsonObject resultObject = new JsonObject();
                resultObject.addProperty("item", Registries.ITEM.getId(outputStack.getItem()).toString());
                if (outputStack.getCount() > 1) {
                    resultObject.addProperty("count", outputStack.getCount());
                }
                json.add("result", resultObject);

                if (experience != 0.0F) {
                    json.addProperty("experience", experience);
                }
                json.addProperty("duration", brewTime);
            }

            @Override
            public Identifier getRecipeId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return ModRecipes.SMALL_CAULDRON_SERIALIZER;
            }

            @Override
            public JsonObject toAdvancementJson() {
                return advancementBuilder.toJson();
            }

            @Override
            public Identifier getAdvancementId() {
                return advancementId;
            }
        });
    }
}
