package net.grapes.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SmallCauldronRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;

    public SmallCauldronRecipe (Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

        boolean[] slotsMatched = new boolean[inventory.size()];
        for (int i = 0; i < slotsMatched.length; i++) {
            slotsMatched[i] = false;
        }

        for (Ingredient ingredient : recipeItems) {
            boolean foundIngredient = false;
            for (int i = 0; i < inventory.size(); i++) {
                if (slotsMatched[i]) {
                    continue;
                }

                if (ingredient.test(inventory.getStack(i))) {
                    foundIngredient = true;
                    slotsMatched[i] = true;
                    break;
                }
            }
            if (!foundIngredient) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= recipeItems.size();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

     public static class Type implements RecipeType<SmallCauldronRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "small_cauldron";
     }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "small_cauldron";

        @Override
        public SmallCauldronRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(3, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new SmallCauldronRecipe(id, output, inputs);
        }

        @Override
        public SmallCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new SmallCauldronRecipe(id, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
        }
    }

}
