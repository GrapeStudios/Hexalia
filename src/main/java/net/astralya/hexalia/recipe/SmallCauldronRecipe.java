package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.astralya.hexalia.item.ModItems;
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

public final class SmallCauldronRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    private final float experience;
    private final int duration;

    public SmallCauldronRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output, float experience, int duration) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
        this.experience = experience;
        this.duration = duration;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient) {
            return false;
        }

        int inputCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getStack(i).isEmpty()) {
                inputCount++;
            }
        }

        if (inputCount != ingredients.size()) {
            return false;
        }

        boolean[] used = new boolean[inventory.size()];

        for (Ingredient ingredient : ingredients) {
            boolean found = false;

            for (int i = 0; i < inventory.size(); i++) {
                if (!used[i] && ingredient.test(inventory.getStack(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }

            if (!found) {
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
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public float getExperience() {
        return experience;
    }

    public int getDuration() {
        return duration;
    }

    public int getBrewTime() {
        return duration;
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

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.SMALL_CAULDRON);
    }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {

        @Override
        public SmallCauldronRecipe read(Identifier id, JsonObject json) {
            JsonArray ingredientsArray = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientsArray.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredientsArray.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(ingredientsArray.get(i)));
            }

            ItemStack output = outputFromJson(JsonHelper.getObject(json, "result"));
            float experience = JsonHelper.getFloat(json, "experience", 0.0F);
            int duration = JsonHelper.getInt(json, "duration", 200);

            return new SmallCauldronRecipe(id, ingredients, output, experience, duration);
        }

        @Override
        public SmallCauldronRecipe read(Identifier id, PacketByteBuf buf) {
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            float experience = buf.readFloat();
            int duration = buf.readInt();

            return new SmallCauldronRecipe(id, ingredients, output, experience, duration);
        }

        @Override
        public void write(PacketByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.output.copy());
            buf.writeFloat(recipe.experience);
            buf.writeInt(recipe.duration);
        }

        private static ItemStack outputFromJson(JsonObject json) {
            String itemId = JsonHelper.getString(json, "item");
            Item item = Registries.ITEM.get(new Identifier(itemId));
            int count = JsonHelper.getInt(json, "count", 1);
            return new ItemStack(item, count);
        }
    }
}
