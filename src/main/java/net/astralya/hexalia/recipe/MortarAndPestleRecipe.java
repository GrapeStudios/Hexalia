package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record MortarAndPestleRecipe(NonNullList<Ingredient> ingredients, ItemStack output) implements Recipe<MortarAndPestleRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(MortarAndPestleRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        List<ItemStack> stacks = new ArrayList<>(3);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        if (stacks.size() != ingredients.size()) {
            return false;
        }

        return matchesShapeless(ingredients, stacks);
    }

    @Override
    public ItemStack assemble(MortarAndPestleRecipeInput input, HolderLookup.Provider provider) {
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
        return ModRecipes.MORTAR_AND_PESTLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MORTAR_AND_PESTLE_TYPE.get();
    }

    private static boolean matchesShapeless(List<Ingredient> ingredients, List<ItemStack> stacks) {
        boolean[] used = new boolean[stacks.size()];
        return matchFrom(ingredients, stacks, used, 0);
    }

    private static boolean matchFrom(List<Ingredient> ingredients, List<ItemStack> stacks, boolean[] used, int idx) {
        if (idx >= ingredients.size()) {
            return true;
        }

        Ingredient ingredient = ingredients.get(idx);
        for (int i = 0; i < stacks.size(); i++) {
            if (used[i]) {
                continue;
            }
            if (!ingredient.test(stacks.get(i))) {
                continue;
            }
            used[i] = true;
            if (matchFrom(ingredients, stacks, used, idx + 1)) {
                return true;
            }
            used[i] = false;
        }

        return false;
    }

    public static class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {

        private static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC =
                Ingredient.CODEC_NONEMPTY
                        .listOf()
                        .flatXmap(
                                list -> {
                                    int size = list.size();
                                    if (size < 1 || size > 3) {
                                        return DataResult.error(() -> "mortar_and_pestle ingredients must have 1 to 3 entries");
                                    }
                                    NonNullList<Ingredient> nn = NonNullList.create();
                                    nn.addAll(list);
                                    return DataResult.success(nn);
                                },
                                DataResult::success
                        );

        public static final MapCodec<MortarAndPestleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(MortarAndPestleRecipe::ingredients),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.output.getItem()),
                Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.output.getCount())
        ).apply(inst, (ings, item, count) -> new MortarAndPestleRecipe(ings, new ItemStack(item, Math.max(1, count)))));

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

        private static MortarAndPestleRecipe read(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> ings = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ings.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }

            ItemStack out = ItemStack.STREAM_CODEC.decode(buf);
            return new MortarAndPestleRecipe(ings, out);
        }

        private static void write(RegistryFriendlyByteBuf buf, MortarAndPestleRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
        }
    }
}