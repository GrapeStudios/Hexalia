package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.util.GsonHelper;

public class SmallCauldronRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final Ingredient bottleSlot;
    private final int brewTime;
    private final float experience;

    public SmallCauldronRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems,
                               Ingredient bottleSlot, int brewTime, float experience) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.bottleSlot = bottleSlot;
        this.brewTime = brewTime;
        this.experience = experience;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        boolean[] slotsMatched = new boolean[container.getContainerSize()];
        for (Ingredient ingredient : recipeItems) {
            boolean foundIngredient = false;
            for (int i = 0; i < container.getContainerSize() - 1; i++) {  // Exclude bottle slot
                if (slotsMatched[i]) {
                    continue;
                }
                if (ingredient.test(container.getItem(i))) {
                    slotsMatched[i] = true;
                    foundIngredient = true;
                    break;
                }
            }
            if (!foundIngredient) {
                return false;
            }
        }

        return bottleSlot.test(container.getItem(container.getContainerSize() - 1));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
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

    public static class Type implements RecipeType<SmallCauldronRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "small_cauldron";
    }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SmallCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Ingredient bottleSlot = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "bottle_slot"));

            // Read brewTime and experience from JSON, with default values if not present
            int brewTime = GsonHelper.getAsInt(json, "brew_time", 175); // Default to 175 ticks
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0f); // Default to no experience

            return new SmallCauldronRecipe(id, output, inputs, bottleSlot, brewTime, experience);
        }

        @Override
        public SmallCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            Ingredient bottleSlot = Ingredient.fromNetwork(buf);
            int brewTime = buf.readInt();
            float experience = buf.readFloat();

            return new SmallCauldronRecipe(id, output, inputs, bottleSlot, brewTime, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeInt(recipe.recipeItems.size());
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.getResultItem(null));
            recipe.bottleSlot.toNetwork(buf);
            buf.writeInt(recipe.brewTime);
            buf.writeFloat(recipe.experience);
        }
    }
}