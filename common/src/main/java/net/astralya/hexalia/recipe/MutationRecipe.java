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

public record MutationRecipe(Ingredient inputItem, ItemStack output)
    implements Recipe<MutationRecipeInput> {
  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> ingredients = NonNullList.create();
    ingredients.add(inputItem);
    return ingredients;
  }

  @Override
  public boolean matches(MutationRecipeInput input, Level level) {
    return !level.isClientSide() && inputItem.test(input.getItem(0));
  }

  @Override
  public ItemStack assemble(MutationRecipeInput input, HolderLookup.Provider registries) {
    return output.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    return output;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return ModRecipeTypes.MUTATION_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipeTypes.MUTATION.get();
  }

  public static final class Serializer implements RecipeSerializer<MutationRecipe> {
    private static final MapCodec<MutationRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        Ingredient.CODEC_NONEMPTY
                            .fieldOf("input")
                            .forGetter(MutationRecipe::inputItem),
                        BuiltInRegistries.ITEM
                            .byNameCodec()
                            .fieldOf("output")
                            .xmap(ItemStack::new, ItemStack::getItem)
                            .forGetter(MutationRecipe::output))
                    .apply(instance, MutationRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, MutationRecipe> STREAM_CODEC =
        StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            MutationRecipe::inputItem,
            ItemStack.STREAM_CODEC,
            MutationRecipe::output,
            MutationRecipe::new);

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
