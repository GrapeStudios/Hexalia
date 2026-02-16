package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SmallCauldronRecipe implements Recipe<RecipeInput> {

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    private final float experience;
    private final int brewTime;

    public SmallCauldronRecipe(NonNullList<Ingredient> ingredients, ItemStack output, float experience, int brewTime) {
        this.ingredients = ingredients;
        this.output = output;
        this.experience = experience;
        this.brewTime = brewTime;
    }

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
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        int inputCount = 0;
        for (int i = 0; i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) inputCount++;
        }

        if (inputCount != ingredients.size()) {
            return false;
        }

        boolean[] used = new boolean[input.size()];

        for (Ingredient ingredient : ingredients) {
            boolean found = false;

            for (int i = 0; i < input.size(); i++) {
                if (!used[i] && ingredient.test(input.getItem(i))) {
                    used[i] = true;
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
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMALL_CAULDRON_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SMALL_CAULDRON_TYPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.SMALL_CAULDRON.get());
    }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {

        private static final Codec<ItemStack> RESULT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
                net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
        ).apply(inst, (item, count) -> new ItemStack(item, count)));

        private static final MapCodec<SmallCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(list -> {
                    NonNullList<Ingredient> nn = NonNullList.create();
                    nn.addAll(list);
                    return nn;
                }, nn -> nn).forGetter(SmallCauldronRecipe::getIngredients),
                RESULT_CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(SmallCauldronRecipe::getExperience),
                Codec.INT.optionalFieldOf("brewtime", 200).forGetter(SmallCauldronRecipe::getBrewTime)
        ).apply(inst, (ingredients, output, experience, time) -> new SmallCauldronRecipe(ingredients, output, experience, time)));

        public static final StreamCodec<RegistryFriendlyByteBuf, SmallCauldronRecipe> STREAM_CODEC =
                StreamCodec.of(SmallCauldronRecipe.Serializer::toNetwork, SmallCauldronRecipe.Serializer::fromNetwork);

        @Override
        public MapCodec<SmallCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmallCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static SmallCauldronRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);
            inputItemsIn.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));

            ItemStack outputIn = ItemStack.STREAM_CODEC.decode(buffer);
            float experienceIn = buffer.readFloat();
            int brewTimeIn = buffer.readVarInt();

            return new SmallCauldronRecipe(inputItemsIn, outputIn, experienceIn, brewTimeIn);
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
