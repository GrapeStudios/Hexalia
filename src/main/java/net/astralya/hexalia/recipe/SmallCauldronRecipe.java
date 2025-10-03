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
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SmallCauldronRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;
    private final Ingredient bottleSlot;
    private final int brewTime;
    private final float experience;

    public SmallCauldronRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems,
                               Ingredient bottleSlot, int brewTime, float experience) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.bottleSlot = bottleSlot;
        this.brewTime = brewTime;
        this.experience = experience;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient) return false;

        boolean[] slotsMatched = new boolean[inventory.size()];
        for (Ingredient ingredient : recipeItems) {
            boolean found = false;
            for (int i = 0; i < inventory.size(); i++) {
                if (slotsMatched[i]) continue;
                if (ingredient.test(inventory.getStack(i))) {
                    slotsMatched[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
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

    public int getBrewTime() {
        return brewTime;
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMALL_CAULDRON_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SMALL_CAULDRON_TYPE;
    }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {
        @Override
        public SmallCauldronRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));
            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            Ingredient bottleSlot = Ingredient.fromJson(JsonHelper.getObject(json, "bottle_slot"));
            int brewTime = JsonHelper.getInt(json, "brew_time", 175);
            float experience = JsonHelper.getFloat(json, "experience", 5.0f);
            return new SmallCauldronRecipe(id, output, inputs, bottleSlot, brewTime, experience);
        }

        @Override
        public SmallCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            Ingredient bottleSlot = Ingredient.fromPacket(buf);
            int brewTime = buf.readInt();
            float experience = buf.readFloat();
            return new SmallCauldronRecipe(id, output, inputs, bottleSlot, brewTime, experience);
        }

        @Override
        public void write(PacketByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.output.copy());
            recipe.getBottleSlot().write(buf);
            buf.writeInt(recipe.brewTime);
            buf.writeFloat(recipe.experience);
        }
    }
}