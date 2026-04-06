package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record RitualBrazierRecipe(Ingredient inputItem, ItemStack output)
        implements Recipe<RitualBrazierRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(RitualBrazierRecipeInput input, World world) {
        if (world.isClient) return false;
        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RitualBrazierRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_BRAZIER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_BRAZIER_TYPE;
    }

    public static class Serializer implements RecipeSerializer<RitualBrazierRecipe> {
        public static final MapCodec<RitualBrazierRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(RitualBrazierRecipe::inputItem),
                ItemStack.CODEC.fieldOf("output").forGetter(RitualBrazierRecipe::output)
        ).apply(inst, RitualBrazierRecipe::new));

        public static final PacketCodec<RegistryByteBuf, RitualBrazierRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, RitualBrazierRecipe::inputItem,
                        ItemStack.PACKET_CODEC, RitualBrazierRecipe::output,
                        RitualBrazierRecipe::new
                );

        @Override
        public MapCodec<RitualBrazierRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, RitualBrazierRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}