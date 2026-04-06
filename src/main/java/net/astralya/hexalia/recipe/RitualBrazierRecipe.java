package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record RitualBrazierRecipe(Ingredient inputItem, ItemStack output) implements Recipe<RitualBrazierRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list  = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(RitualBrazierRecipeInput input, Level level) {
        if (level.isClientSide()){
            return false;
        }
        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RitualBrazierRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_BRAZIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_BRAZIER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<RitualBrazierRecipe> {
        public static final MapCodec<RitualBrazierRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(RitualBrazierRecipe::inputItem),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").xmap(
                        ItemStack::new,
                        ItemStack::getItem
                ).forGetter(RitualBrazierRecipe::output)
        ).apply(inst, RitualBrazierRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RitualBrazierRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, RitualBrazierRecipe::inputItem,
                        ItemStack.STREAM_CODEC, RitualBrazierRecipe::output,
                        RitualBrazierRecipe::new
                );

        @Override
        public MapCodec<RitualBrazierRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RitualBrazierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
