package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
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

public record MortarAndPestleRecipe(NonNullList<Ingredient> ingredients, ItemStack output)
    implements Recipe<MortarAndPestleRecipeInput> {
  @Override
  public NonNullList<Ingredient> getIngredients() {
    return ingredients;
  }

  @Override
  public boolean matches(MortarAndPestleRecipeInput input, Level level) {
    if (level.isClientSide()) {
      return false;
    }
    List<ItemStack> stacks = new ArrayList<>(3);
    for (int index = 0; index < input.size(); index++) {
      ItemStack stack = input.getItem(index);
      if (!stack.isEmpty()) {
        stacks.add(stack);
      }
    }
    return stacks.size() == ingredients.size() && matchesShapeless(ingredients, stacks);
  }

  @Override
  public ItemStack assemble(MortarAndPestleRecipeInput input, HolderLookup.Provider registries) {
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
    return ModRecipeTypes.MORTAR_AND_PESTLE_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipeTypes.MORTAR_AND_PESTLE.get();
  }

  private static boolean matchesShapeless(List<Ingredient> ingredients, List<ItemStack> stacks) {
    return matchFrom(ingredients, stacks, new boolean[stacks.size()], 0);
  }

  private static boolean matchFrom(
      List<Ingredient> ingredients, List<ItemStack> stacks, boolean[] used, int ingredientIndex) {
    if (ingredientIndex >= ingredients.size()) {
      return true;
    }

    Ingredient ingredient = ingredients.get(ingredientIndex);
    for (int index = 0; index < stacks.size(); index++) {
      if (used[index] || !ingredient.test(stacks.get(index))) {
        continue;
      }
      used[index] = true;
      if (matchFrom(ingredients, stacks, used, ingredientIndex + 1)) {
        return true;
      }
      used[index] = false;
    }
    return false;
  }

  public static final class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {
    private static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC =
        Ingredient.CODEC_NONEMPTY
            .listOf()
            .flatXmap(
                list -> {
                  if (list.isEmpty() || list.size() > 3) {
                    return DataResult.error(
                        () -> "mortar_and_pestle ingredients must have 1 to 3 entries");
                  }
                  NonNullList<Ingredient> ingredients = NonNullList.create();
                  ingredients.addAll(list);
                  return DataResult.success(ingredients);
                },
                DataResult::success);

    public static final MapCodec<MortarAndPestleRecipe> CODEC =
        RecordCodecBuilder.mapCodec(
            instance ->
                instance
                    .group(
                        INGREDIENTS_CODEC
                            .fieldOf("ingredients")
                            .forGetter(MortarAndPestleRecipe::ingredients),
                        BuiltInRegistries.ITEM
                            .byNameCodec()
                            .fieldOf("result")
                            .forGetter(recipe -> recipe.output.getItem()),
                        Codec.INT
                            .optionalFieldOf("count", 1)
                            .forGetter(recipe -> recipe.output.getCount()))
                    .apply(
                        instance,
                        (ingredients, item, count) ->
                            new MortarAndPestleRecipe(
                                ingredients, new ItemStack(item, Math.max(1, count)))));

    public static final StreamCodec<RegistryFriendlyByteBuf, MortarAndPestleRecipe> STREAM_CODEC =
        StreamCodec.of(Serializer::write, Serializer::read);

    @Override
    public MapCodec<MortarAndPestleRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MortarAndPestleRecipe> streamCodec() {
      return STREAM_CODEC;
    }

    private static MortarAndPestleRecipe read(RegistryFriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
      for (int index = 0; index < size; index++) {
        ingredients.set(index, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
      }
      return new MortarAndPestleRecipe(ingredients, ItemStack.STREAM_CODEC.decode(buffer));
    }

    private static void write(RegistryFriendlyByteBuf buffer, MortarAndPestleRecipe recipe) {
      buffer.writeVarInt(recipe.ingredients.size());
      for (Ingredient ingredient : recipe.ingredients) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
      }
      ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
    }
  }
}
