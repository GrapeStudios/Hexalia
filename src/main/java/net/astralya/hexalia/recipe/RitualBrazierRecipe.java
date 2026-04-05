package net.astralya.hexalia.recipe;

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

public final class RitualBrazierRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final Ingredient inputItem;
    private final ItemStack output;

    public RitualBrazierRecipe(Identifier id, Ingredient inputItem, ItemStack output) {
        this.id = id;
        this.inputItem = inputItem;
        this.output = output.copy();
    }

    public Ingredient inputItem() {
        return this.inputItem;
    }

    public ItemStack output() {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean matches(SimpleInventory inv, World world) {
        if (world.isClient) return false;
        return this.inputItem.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inv, DynamicRegistryManager registries) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registries) {
        return this.output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(this.inputItem);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_BRAZIER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_BRAZIER_TYPE;
    }

    public static final class Serializer implements RecipeSerializer<RitualBrazierRecipe> {
        @Override
        public RitualBrazierRecipe read(Identifier id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("input"));
            ItemStack out = ShapedRecipe.outputFromJson(json.getAsJsonObject("output"));
            return new RitualBrazierRecipe(id, input, out);
        }

        @Override
        public RitualBrazierRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack out = buf.readItemStack();
            return new RitualBrazierRecipe(id, input, out);
        }

        @Override
        public void write(PacketByteBuf buf, RitualBrazierRecipe recipe) {
            recipe.inputItem.write(buf);
            buf.writeItemStack(recipe.output.copy());
        }
    }
}
