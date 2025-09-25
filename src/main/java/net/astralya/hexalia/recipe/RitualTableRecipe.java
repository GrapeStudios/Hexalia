package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class RitualTableRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;

    public static final int INPUT_SLOTS = 4;

    public RitualTableRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level level) {
        if (level.isClientSide) return false;
        if (ingredients.isEmpty()) return false;
        return ingredients.get(0).test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer inv, RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public ResourceLocation getId() {
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

    public static class Type implements RecipeType<RitualTableRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ritual_table";
    }

    public static class Serializer implements RecipeSerializer<RitualTableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RitualTableRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray arr = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> list = NonNullList.create();
            for (int i = 0; i < arr.size(); i++) {
                list.add(Ingredient.fromJson(arr.get(i)));
            }
            if (list.isEmpty()) {
                throw new IllegalArgumentException("RitualTable: ingredients array cannot be empty");
            }
            ItemStack out = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            return new RitualTableRecipe(id, list, out);
        }

        @Override
        public RitualTableRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int count = buf.readVarInt();
            NonNullList<Ingredient> list = NonNullList.withSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) list.set(i, Ingredient.fromNetwork(buf));
            ItemStack out = buf.readItem();
            return new RitualTableRecipe(id, list, out);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RitualTableRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buf);
            buf.writeItem(recipe.output);
        }
    }
}