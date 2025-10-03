package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
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

public class RitualTableRecipe implements Recipe<RitualTableBlockEntity> {

    private final Identifier id;
    private final ItemStack input;
    private final ItemStack output;
    private final List<ItemStack> saltItems;

    public RitualTableRecipe(Identifier id, ItemStack input, ItemStack output, List<ItemStack> saltItems) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.saltItems = saltItems;
    }

    @Override
    public boolean matches(RitualTableBlockEntity blockEntity, World world) {
        ItemStack itemInSlot = blockEntity.getStack(0);

        if (!ItemStack.areEqual(itemInSlot, input)) {
            return false;
        }

        return blockEntity.processSaltBlocks(world, blockEntity.getPos(), this, false);
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

    public static class Type implements RecipeType<RitualTableRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "transmutation";

        private Type() {}
    }

    public static class Serializer implements RecipeSerializer<RitualTableRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "transmutation";

        @Override
        public RitualTableRecipe read(Identifier id, JsonObject json) {
            ItemStack input = new ItemStack(JsonHelper.getItem(json.get("input").getAsJsonObject(), "item"));
            ItemStack output = new ItemStack(JsonHelper.getItem(json.get("output").getAsJsonObject(), "item"));
            JsonArray saltItemsJson = JsonHelper.getArray(json, "salt_items");
            List<ItemStack> saltItems = new ArrayList<>();
            for (JsonElement element : saltItemsJson) {
                ItemStack saltItem = new ItemStack(JsonHelper.getItem(element.getAsJsonObject(), "item"));
                saltItems.add(saltItem);
            }
            return new RitualTableRecipe(id, input, output, saltItems);
        }

        @Override
        public RitualTableRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack input = buf.readItemStack();
            ItemStack output = buf.readItemStack();
            int size = buf.readInt();
            List<ItemStack> saltItems = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                saltItems.add(buf.readItemStack());
            }
            return new RitualTableRecipe(id, input, output, saltItems);
        }

        @Override
        public void write(PacketByteBuf buf, RitualTableRecipe recipe) {
            buf.writeItemStack(recipe.input);
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.saltItems.size());
            for (ItemStack stack : recipe.saltItems) {
                buf.writeItemStack(stack);
            }
        }
    }
}
