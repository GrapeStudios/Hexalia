package net.astralya.hexalia.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class RitualBrazierRecipe implements Recipe<SimpleInventory> {

    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;

    public RitualBrazierRecipe(Identifier id, ItemStack output, Ingredient input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }
        return input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
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

    public Ingredient getInput() {
        return input;
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

    public static class Type implements RecipeType<RitualBrazierRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "ritual_brazier";
    }

    public static class Serializer implements RecipeSerializer<RitualBrazierRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "ritual_brazier";

        @Override
        public RitualBrazierRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));
            Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "input"));
            return new RitualBrazierRecipe(id, output, input);
        }

        @Override
        public RitualBrazierRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            return new RitualBrazierRecipe(id, output, input);
        }

        @Override
        public void write(PacketByteBuf buf, RitualBrazierRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}

