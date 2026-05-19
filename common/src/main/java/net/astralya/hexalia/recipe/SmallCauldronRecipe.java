package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class SmallCauldronRecipe implements Recipe<RecipeInput> {
  private final NonNullList<Ingredient> ingredients;
  private final ItemStack output;
  private final float experience;
  private final int brewTime;

  public SmallCauldronRecipe(
      NonNullList<Ingredient> ingredients, ItemStack output, float experience, int brewTime) {
    this.ingredients = ingredients;
    this.output = output;
    this.experience = experience;
    this.brewTime = brewTime;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return ingredients;
  }

  public float getExperience() {
    return experience;
  }

  public int getBrewTime() {
    return brewTime;
  }

  @Override
  public boolean matches(RecipeInput input, Level level) {
    int inputCount = 0;
    for (int index = 0; index < input.size(); index++) {
      if (!input.getItem(index).isEmpty()) {
        inputCount++;
      }
    }
    if (inputCount != ingredients.size()) {
      return false;
    }

    boolean[] used = new boolean[input.size()];
    for (Ingredient ingredient : ingredients) {
      boolean found = false;
      for (int index = 0; index < input.size(); index++) {
        if (!used[index] && ingredient.test(input.getItem(index))) {
          used[index] = true;
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
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
    return ModRecipeTypes.SMALL_CAULDRON_SERIALIZER.get();
  }

  @Override
  public RecipeType<?> getType() {
    return ModRecipeTypes.SMALL_CAULDRON.get();
  }

  @Override
  public ItemStack getToastSymbol() {
    return new ItemStack(ModItems.SMALL_CAULDRON.get());
  }

  public static final class Serializer implements RecipeSerializer<SmallCauldronRecipe> {
    private static final Codec<ItemStack> RESULT_CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        BuiltInRegistries.ITEM
                            .byNameCodec()
                            .fieldOf("item")
                            .forGetter(ItemStack::getItem),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount))
                    .apply(instance, ItemStack::new));

    private static final MapCodec<SmallCauldronRecipe> CODEC =
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
                            .forGetter(SmallCauldronRecipe::getIngredients),
                        RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                        Codec.FLOAT
                            .optionalFieldOf("experience", 0.0F)
                            .forGetter(SmallCauldronRecipe::getExperience),
                        Codec.INT
                            .optionalFieldOf("brewtime", 200)
                            .forGetter(SmallCauldronRecipe::getBrewTime))
                    .apply(instance, SmallCauldronRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmallCauldronRecipe> STREAM_CODEC =
        StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

    @Override
    public MapCodec<SmallCauldronRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SmallCauldronRecipe> streamCodec() {
      return STREAM_CODEC;
    }

    private static SmallCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
      int count = buffer.readVarInt();
      NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
      for (int index = 0; index < count; index++) {
        ingredients.set(index, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
      }
      ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
      float experience = buffer.readFloat();
      int brewTime = buffer.readVarInt();
      return new SmallCauldronRecipe(ingredients, output, experience, brewTime);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, SmallCauldronRecipe recipe) {
      buffer.writeVarInt(recipe.ingredients.size());
      for (Ingredient ingredient : recipe.ingredients) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
      }
      ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
      buffer.writeFloat(recipe.experience);
      buffer.writeVarInt(recipe.brewTime);
    }
  }
}
