package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record MutationRecipe(Ingredient inputItem, ItemStack output)
        implements Recipe<MutationRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(MutationRecipeInput input, World world) {
        if (world.isClient) return false;
        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(MutationRecipeInput input, RegistryWrapper.WrapperLookup registries) {
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
        return ModRecipes.MUTATION_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MUTATION_TYPE;
    }

    public static class Serializer implements RecipeSerializer<MutationRecipe> {
        public static final MapCodec<MutationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(MutationRecipe::inputItem),
                ItemStack.CODEC.fieldOf("output").forGetter(MutationRecipe::output)
        ).apply(inst, MutationRecipe::new));

        public static final PacketCodec<RegistryByteBuf, MutationRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, MutationRecipe::inputItem,
                        ItemStack.PACKET_CODEC, MutationRecipe::output,
                        MutationRecipe::new
                );

        @Override
        public MapCodec<MutationRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MutationRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}