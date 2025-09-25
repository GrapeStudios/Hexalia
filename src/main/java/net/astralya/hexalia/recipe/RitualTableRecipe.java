package net.astralya.hexalia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.astralya.hexalia.block.entity.custom.RitualTableBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RitualTableRecipe implements Recipe<RitualTableBlockEntity> {

    private final ResourceLocation id;
    private final ItemStack input;
    private final ItemStack output;
    private final List<ItemStack> saltItems;

    public RitualTableRecipe(ResourceLocation id, ItemStack input, ItemStack output, List<ItemStack> saltItems) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.saltItems = saltItems;
    }

    @Override
    public boolean matches(RitualTableBlockEntity blockEntity, Level world) {
        ItemStack itemInSlot = blockEntity.getItem(0);

        if (!ItemStack.isSameItem(itemInSlot, input)) {
            return false;
        }

        return blockEntity.processSaltBlocks(world, blockEntity.getBlockPos(), this, false);
    }

    @Override
    public ItemStack assemble(RitualTableBlockEntity pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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

    public List<ItemStack> getSaltItems() {
        return saltItems;
    }

    public ItemStack getInput() {
        return input;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RitualTableRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "transmutation";
    }

    public static class Serializer implements RecipeSerializer<RitualTableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public RitualTableRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            List<ItemStack> saltItems = new ArrayList<>();
            JsonArray saltArray = GsonHelper.getAsJsonArray(json, "salt_items");
            for (JsonElement element : saltArray) {
                saltItems.add(ShapedRecipe.itemStackFromJson(element.getAsJsonObject()));
            }

            return new RitualTableRecipe(id, input, output, saltItems);
        }

        @Override
        public RitualTableRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ItemStack input = buffer.readItem();
            ItemStack output = buffer.readItem();
            int saltSize = buffer.readInt();
            List<ItemStack> saltItems = new ArrayList<>();

            for (int i = 0; i < saltSize; i++) {
                saltItems.add(buffer.readItem());
            }

            return new RitualTableRecipe(id, input, output, saltItems);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RitualTableRecipe recipe) {
            buffer.writeItem(recipe.input);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.saltItems.size());

            for (ItemStack saltItem : recipe.saltItems) {
                buffer.writeItem(saltItem);
            }
        }
    }
}
