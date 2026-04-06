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

public record RitualTableRecipe(NonNullList<Ingredient> ingredients, ItemStack output) implements Recipe<RitualTableRecipeInput> {

    public static final int INPUT_SLOTS = 4;

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(RitualTableRecipeInput inv, Level lvl) {
        if (lvl.isClientSide()) return false;

        if (ingredients.isEmpty() || !ingredients.get(0).test(inv.getItem(0))) {
            return false;
        }

        if (ingredients.size() == 1) {
            return true;
        }

        return true;
    }


    @Override public ItemStack assemble(RitualTableRecipeInput inv,
                                        HolderLookup.Provider reg) { return output.copy(); }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_TABLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<RitualTableRecipe> {
        public static final MapCodec<RitualTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients")
                        .xmap(list -> {
                            NonNullList<Ingredient> nn = NonNullList.create();
                            nn.addAll(list);
                            return nn;
                        }, list -> list)
                        .forGetter(RitualTableRecipe::ingredients),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").xmap(
                        ItemStack::new, ItemStack::getItem
                ).forGetter(RitualTableRecipe::output)
        ).apply(inst, RitualTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RitualTableRecipe> STREAM_CODEC =
                StreamCodec.of(
                        // Writer
                        (buf, recipe) -> {
                            buf.writeVarInt(recipe.ingredients.size());
                            for (Ingredient ing : recipe.ingredients) {
                                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                            }
                            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                        },
                        // Reader
                        (buf) -> {
                            int count = buf.readVarInt();
                            NonNullList<Ingredient> list = NonNullList.withSize(count, Ingredient.EMPTY);
                            for (int i = 0; i < count; i++) {
                                list.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                            }
                            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                            return new RitualTableRecipe(list, output);
                        }
                );

        @Override
        public MapCodec<RitualTableRecipe> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RitualTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static RitualTableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredientsList = NonNullList.withSize(count, Ingredient.EMPTY);
            ingredientsList.replaceAll(ing -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack outputStack = ItemStack.STREAM_CODEC.decode(buffer);
            return new RitualTableRecipe(ingredientsList, outputStack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, RitualTableRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
