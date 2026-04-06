package net.astralya.hexalia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SmallCauldronRecipe implements Recipe<RecipeInput> {

    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    private final float experience;
    private final int brewTime;

    public SmallCauldronRecipe(DefaultedList<Ingredient> ingredients, ItemStack output, float experience, int brewTime) {
        this.ingredients = ingredients;
        this.output = output;
        this.experience = experience;
        this.brewTime = brewTime;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public float getExperience() {
        return experience;
    }

    public int getBrewTime() {
        return brewTime;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public boolean matches(RecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }

        int inputCount = 0;
        for (int i = 0; i < input.getSize(); i++) {
            if (!input.getStackInSlot(i).isEmpty()) {
                inputCount++;
            }
        }

        if (inputCount != ingredients.size()) {
            return false;
        }

        boolean[] used = new boolean[input.getSize()];

        for (Ingredient ingredient : ingredients) {
            boolean found = false;

            for (int i = 0; i < input.getSize(); i++) {
                if (!used[i] && ingredient.test(input.getStackInSlot(i))) {
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
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SMALL_CAULDRON_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SMALL_CAULDRON_TYPE;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.SMALL_CAULDRON);
    }

    public static class Serializer implements RecipeSerializer<SmallCauldronRecipe> {

        private static final Codec<ItemStack> RESULT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemStack::getItem),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
        ).apply(inst, (item, count) -> new ItemStack(item, count)));

        private static final MapCodec<SmallCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").xmap(list -> {
                    DefaultedList<Ingredient> nn = DefaultedList.of();
                    nn.addAll(list);
                    return nn;
                }, nn -> nn).forGetter(SmallCauldronRecipe::getIngredients),
                RESULT_CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(SmallCauldronRecipe::getExperience),
                Codec.INT.optionalFieldOf("brewtime", 200).forGetter(SmallCauldronRecipe::getBrewTime)
        ).apply(inst, SmallCauldronRecipe::new));

        public static final PacketCodec<RegistryByteBuf, SmallCauldronRecipe> STREAM_CODEC =
                PacketCodec.of(
                        (recipe, buf) -> write(buf, recipe),
                        Serializer::read
                );

        @Override
        public MapCodec<SmallCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SmallCauldronRecipe> packetCodec() {
            return STREAM_CODEC;
        }

        private static SmallCauldronRecipe read(RegistryByteBuf buf) {
            int size = buf.readVarInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.PACKET_CODEC.decode(buf));
            }

            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            float experience = buf.readFloat();
            int brewTime = buf.readVarInt();

            return new SmallCauldronRecipe(ingredients, output, experience, brewTime);
        }

        private static void write(RegistryByteBuf buf, SmallCauldronRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }

            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.brewTime);
        }
    }
}