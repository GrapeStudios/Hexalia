package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class MortarAndPestleRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;

    public MortarAndPestleRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient) {
            return false;
        }

        List<ItemStack> stacks = new ArrayList<>(3);
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        if (stacks.size() != this.ingredients.size()) {
            return false;
        }

        boolean[] used = new boolean[stacks.size()];
        return matchIngredients(this.ingredients, stacks, used, 0);
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MORTAR_AND_PESTLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MORTAR_AND_PESTLE_TYPE;
    }

    private static boolean matchIngredients(List<Ingredient> ingredients, List<ItemStack> stacks, boolean[] used, int index) {
        if (index >= ingredients.size()) {
            return true;
        }

        Ingredient ingredient = ingredients.get(index);
        for (int i = 0; i < stacks.size(); i++) {
            if (used[i]) {
                continue;
            }
            if (!ingredient.test(stacks.get(i))) {
                continue;
            }
            used[i] = true;
            if (matchIngredients(ingredients, stacks, used, index + 1)) {
                return true;
            }
            used[i] = false;
        }

        return false;
    }

    public static final class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {

        @Override
        public MortarAndPestleRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredientArray = JsonHelper.getArray(json, "ingredients");
            if (ingredientArray.size() < 1 || ingredientArray.size() > 3) {
                throw new IllegalArgumentException("mortar_and_pestle ingredients must have 1 to 3 entries");
            }

            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientArray.size(), Ingredient.EMPTY);
            for (int i = 0; i < ingredientArray.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(ingredientArray.get(i)));
            }

            String itemId = JsonHelper.getString(json, "result");
            Item item = Registries.ITEM.get(new Identifier(itemId));
            int count = Math.max(1, JsonHelper.getInt(json, "count", 1));
            ItemStack output = new ItemStack(item, count);

            return new MortarAndPestleRecipe(id, ingredients, output);
        }

        @Override
        public MortarAndPestleRecipe read(Identifier id, PacketByteBuf buf) {
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new MortarAndPestleRecipe(id, ingredients, output);
        }

        @Override
        public void write(PacketByteBuf buf, MortarAndPestleRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.output.copy());
        }
    }
}