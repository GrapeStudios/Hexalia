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
    private final Ingredient bottleSlot;

    public SmallCauldronRecipe (Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Ingredient bottleSlot) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.bottleSlot = bottleSlot;
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
                    slotsMatched[i] = true;
                    foundIngredient = true;
                    break;
                }
            }
            if (!foundIngredient) {
                return false;
            }
        }

        return bottleSlot.test(inventory.getStack(inventory.size() - 1));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager manager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager manager) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public Ingredient getBottleSlot() {
        return bottleSlot;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SmallCauldronRecipe> {
        private Type() { }
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
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i).getAsJsonObject()));
            }

            Ingredient bottleSlot = Ingredient.fromJson(JsonHelper.getObject(json, "bottle_slot"));

            return new SmallCauldronRecipe(id, output, inputs, bottleSlot);
        }

        @Override
        public SmallCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            Ingredient bottleSlot = Ingredient.fromPacket(buf);
            return new SmallCauldronRecipe(id, output, inputs, bottleSlot);
        }

        @Override
        public void write(PacketByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
            recipe.getBottleSlot().write(buf);
        }
    }
}
