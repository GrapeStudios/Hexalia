package net.astralya.hexalia.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.astralya.hexalia.recipe.ModRecipes;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.AdvancementRewards;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class MortarAndPestleRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final DefaultedList<Ingredient> inputs;
    private final ItemStack outputStack;
    private final Item result;
    private final Map<String, CriterionConditions> criteria = new LinkedHashMap<>();

    private MortarAndPestleRecipeBuilder(DefaultedList<Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.outputStack = output.copy();
        this.result = output.getItem();
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    public static MortarAndPestleRecipeBuilder mortar(Ingredient input1, Ingredient input2, Ingredient input3, ItemStack output) {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);
        return new MortarAndPestleRecipeBuilder(inputs, output);
    }

    @Override
    public MortarAndPestleRecipeBuilder criterion(String name, CriterionConditions criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        return this;
    }

    public MortarAndPestleRecipeBuilder unlockedByItem(String name, ItemConvertible item) {
        return this.criterion(name, InventoryChangedCriterion.Conditions.items(item));
    }

    @Override
    public Item getOutputItem() {
        return this.result;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier id) {
        Identifier advancementId = id.withPrefixedPath("recipes/mortar_and_pestle/");
        Advancement.Builder advancementBuilder = Advancement.Builder.create()
                .parent(ROOT)
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .criteriaMerger(CriterionMerger.OR);

        this.criteria.forEach(advancementBuilder::criterion);

        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
                JsonArray ingredients = new JsonArray();

                for (Ingredient ingredient : inputs) {
                    ingredients.add(ingredient.toJson());
                }

                json.add("ingredients", ingredients);
                json.addProperty("result", Registries.ITEM.getId(result).toString());

                if (outputStack.getCount() > 1) {
                    json.addProperty("count", outputStack.getCount());
                }
            }

            @Override
            public Identifier getRecipeId() {
                return id;
            }

            @Override
            public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
                return ModRecipes.MORTAR_AND_PESTLE_SERIALIZER;
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