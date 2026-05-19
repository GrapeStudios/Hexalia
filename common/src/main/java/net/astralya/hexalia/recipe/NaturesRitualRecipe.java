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

public record NaturesRitualRecipe(NonNullList<Ingredient> ingredients, ItemStack output)
    implements Recipe<NaturesRitualRecipeInput> {
  @Override
  public NonNullList<Ingredient> getIngredients() {
    return ingredients;
  }

  @Override
  public boolean matches(NaturesRitualRecipeInput input, Level level) {
    return !level.isClientSide()
        && !ingredients.isEmpty()
        && ingredients.get(0).test(input.getItem(0));
  }

  @Override
  public ItemStack assemble(NaturesRitualRecipeInput input, HolderLookup.Provider registries) {
    return output.copy();
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= ingredients.size();
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
    return ModRecipeTypes.NATURES_RITUAL_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipeTypes.NATURES_RITUAL.get();
  }

  public static final class Serializer implements RecipeSerializer<NaturesRitualRecipe> {
    public static final MapCodec<NaturesRitualRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        Ingredient.CODEC_NONEMPTY
                            .listOf()
                            .fieldOf("ingredients")
                            .xmap(
                                list -> {
                                  NonNullList<Ingredient> ingredients = NonNullList.create();
                                  ingredients.addAll(list);
                                  return ingredients;
                                },
                                list -> list)
                            .forGetter(NaturesRitualRecipe::ingredients),
                        BuiltInRegistries.ITEM
                            .byNameCodec()
                            .fieldOf("output")
                            .xmap(ItemStack::new, ItemStack::getItem)
                            .forGetter(NaturesRitualRecipe::output))
                    .apply(instance, NaturesRitualRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NaturesRitualRecipe> STREAM_CODEC =
        StreamCodec.of(
            (buffer, recipe) -> {
              buffer.writeVarInt(recipe.ingredients.size());
              for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
              }
              ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            },
            buffer -> {
              int count = buffer.readVarInt();
              NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
              for (int index = 0; index < count; index++) {
                ingredients.set(index, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
              }
              return new NaturesRitualRecipe(ingredients, ItemStack.STREAM_CODEC.decode(buffer));
            });

    @Override
    public MapCodec<NaturesRitualRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, NaturesRitualRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
