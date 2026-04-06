package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public record MortarAndPestleRecipe(DefaultedList<Ingredient> ingredients, ItemStack output) implements Recipe<MortarAndPestleRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(MortarAndPestleRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }
        List<ItemStack> stacks = new ArrayList<>(3);
        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
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
    public ItemStack craft(MortarAndPestleRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MORTAR_AND_PESTLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MORTAR_AND_PESTLE_TYPE;
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

        private static final Codec<DefaultedList<Ingredient>> INGREDIENTS_CODEC =
                Ingredient.DISALLOW_EMPTY_CODEC
                        .listOf()
                        .flatXmap(
                                list -> {
                                    int size = list.size();
                                    if (size < 1 || size > 3) {
                                        return DataResult.error(() -> "mortar_and_pestle ingredients must have 1 to 3 entries");
                                    }
                                    DefaultedList<Ingredient> nn = DefaultedList.of();
                                    nn.addAll(list);
                                    return DataResult.success(nn);
                                },
                                DataResult::success
                        );

        public static final MapCodec<MortarAndPestleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(MortarAndPestleRecipe::ingredients),
                Registries.ITEM.getCodec().fieldOf("result").forGetter(r -> r.output.getItem()),
                Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.output.getCount())
        ).apply(inst, (ings, item, count) -> new MortarAndPestleRecipe(ings, new ItemStack(item, Math.max(1, count)))));

        public static final PacketCodec<RegistryByteBuf, MortarAndPestleRecipe> STREAM_CODEC =
                PacketCodec.of(
                        (recipe, buf) -> write(buf, recipe),
                        Serializer::read
                );

        @Override
        public MapCodec<MortarAndPestleRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MortarAndPestleRecipe> packetCodec() {
            return STREAM_CODEC;
        }

        private static MortarAndPestleRecipe read(RegistryByteBuf buf) {
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ings = DefaultedList.ofSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ings.set(i, Ingredient.PACKET_CODEC.decode(buf));
            }
            ItemStack out = ItemStack.PACKET_CODEC.decode(buf);
            return new MortarAndPestleRecipe(ings, out);
        }

        private static void write(RegistryByteBuf buf, MortarAndPestleRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) {
                Ingredient.PACKET_CODEC.encode(buf, ing);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
        }
    }
}