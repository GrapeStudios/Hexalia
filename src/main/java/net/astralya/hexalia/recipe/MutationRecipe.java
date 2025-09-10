package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record MutationRecipe(Ingredient inputItem, ItemStack output) implements Recipe<MutationRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(MutationRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(MutationRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MUTATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MutationRecipe> {
        public static final MapCodec<MutationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MutationRecipe::inputItem),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").xmap(
                        ItemStack::new,
                        ItemStack::getItem
                ).forGetter(MutationRecipe::output)
        ).apply(inst, MutationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MutationRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, MutationRecipe::inputItem,
                        ItemStack.STREAM_CODEC, MutationRecipe::output,
                        MutationRecipe::new
                );

        @Override
        public MapCodec<MutationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MutationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}