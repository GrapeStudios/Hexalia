package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public final class RitualTableRecipe implements Recipe<SimpleInventory> {

    public static final int INPUT_SLOTS = 4;

    private final Identifier id;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;

    public RitualTableRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output.copy();
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        if (world.isClient) return false;
        if (ingredients.isEmpty()) return false;
        Ingredient first = ingredients.get(0);
        return first.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inv, DynamicRegistryManager registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registries) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_TABLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_TABLE_TYPE;
    }

    public static final class Serializer implements RecipeSerializer<RitualTableRecipe> {
        @Override
        public RitualTableRecipe read(Identifier id, JsonObject json) {
            JsonArray arr = json.getAsJsonArray("ingredients");
            DefaultedList<Ingredient> list = DefaultedList.ofSize(arr.size(), Ingredient.EMPTY);
            for (int i = 0; i < arr.size(); i++) {
                list.set(i, Ingredient.fromJson(arr.get(i)));
            }
            ItemStack output = ShapedRecipe.outputFromJson(json.getAsJsonObject("output"));
            return new RitualTableRecipe(id, list, output);
        }

        @Override
        public RitualTableRecipe read(Identifier id, PacketByteBuf buf) {
            int count = buf.readVarInt();
            DefaultedList<Ingredient> list = DefaultedList.ofSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) {
                list.set(i, Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            return new RitualTableRecipe(id, list, output);
        }

        @Override
        public void write(PacketByteBuf buf, RitualTableRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.output);
        }
    }
}
