package net.grapes.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grapes.hexalia.block.entity.RitualTableBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TransmutationRecipe implements Recipe<RitualTableBlockEntity> {

    private final Identifier id;
    private final ItemStack input;
    private final ItemStack output;
    private final List<ItemStack> saltItems;

    public TransmutationRecipe(Identifier id, ItemStack input, ItemStack output, List<ItemStack> saltItems) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.saltItems = saltItems;
    }

    @Override
    public boolean matches(RitualTableBlockEntity inv, World world) {
        // Check if the inventory matches the recipe requirements
        ItemStack inputStack = inv.getStack(0);
        if (!ItemStack.areItemsEqual(inputStack, this.input)) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(RitualTableBlockEntity inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
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

    public ItemStack getInput() {
        return input;
    }

    public List<ItemStack> getSaltItems() {
        return saltItems;
    }

    public static class Type implements RecipeType<TransmutationRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "transmutation";

        private Type() {}
    }

    public static class Serializer implements RecipeSerializer<TransmutationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "transmutation";

        @Override
        public TransmutationRecipe read(Identifier id, JsonObject json) {
            ItemStack input = new ItemStack(JsonHelper.getItem(json.get("input").getAsJsonObject(), "item"));
            ItemStack output = new ItemStack(JsonHelper.getItem(json.get("output").getAsJsonObject(), "item"));
            JsonArray saltItemsJson = JsonHelper.getArray(json, "salt_items");
            List<ItemStack> saltItems = new ArrayList<>();
            for (JsonElement element : saltItemsJson) {
                ItemStack saltItem = new ItemStack(JsonHelper.getItem(element.getAsJsonObject(), "item"));
                saltItems.add(saltItem);
                // Add debug print statements to verify correct parsing
                System.out.println("Parsed salt item: " + saltItem.getItem().getName().getString());
            }
            System.out.println("Parsed input item: " + input.getItem().getName().getString());
            System.out.println("Parsed output item: " + output.getItem().getName().getString());
            return new TransmutationRecipe(id, input, output, saltItems);
        }

        @Override
        public TransmutationRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack input = buf.readItemStack();
            ItemStack output = buf.readItemStack();
            int size = buf.readInt();
            List<ItemStack> saltItems = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                saltItems.add(buf.readItemStack());
            }
            return new TransmutationRecipe(id, input, output, saltItems);
        }

        @Override
        public void write(PacketByteBuf buf, TransmutationRecipe recipe) {
            buf.writeItemStack(recipe.input);
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.saltItems.size());
            for (ItemStack stack : recipe.saltItems) {
                buf.writeItemStack(stack);
            }
        }
    }
}
