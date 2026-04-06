package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public record RitualTableRecipe(DefaultedList<Ingredient> ingredients, ItemStack output)
        implements Recipe<RitualTableRecipeInput> {

    public static final int INPUT_SLOTS = 4;

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(RitualTableRecipeInput input, World world) {
        if (world.isClient) return false;
        if (ingredients.isEmpty()) return false;
        Ingredient first = ingredients.getFirst();
        return first.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RitualTableRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_TABLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_TABLE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<RitualTableRecipe> {
        public static final MapCodec<RitualTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients")
                        .xmap(list -> {
                            DefaultedList<Ingredient> dl = DefaultedList.ofSize(list.size(), Ingredient.EMPTY);
                            for (int i = 0; i < list.size(); i++) dl.set(i, list.get(i));
                            return dl;
                        }, List::copyOf)
                        .forGetter(RitualTableRecipe::ingredients),
                ItemStack.CODEC.fieldOf("output").forGetter(RitualTableRecipe::output)
        ).apply(inst, RitualTableRecipe::new));

        private static final PacketCodec<RegistryByteBuf, DefaultedList<Ingredient>> INGREDIENTS_PACKET =
                PacketCodec.of(
                        (DefaultedList<Ingredient> list, RegistryByteBuf buf) -> {
                            buf.writeVarInt(list.size());
                            for (Ingredient ing : list) {
                                Ingredient.PACKET_CODEC.encode(buf, ing);
                            }
                        },
                        (RegistryByteBuf buf) -> {
                            int count = buf.readVarInt();
                            DefaultedList<Ingredient> list = DefaultedList.ofSize(count, Ingredient.EMPTY);
                            for (int i = 0; i < count; i++) {
                                list.set(i, Ingredient.PACKET_CODEC.decode(buf));
                            }
                            return list;
                        }
                );

        public static final PacketCodec<RegistryByteBuf, RitualTableRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        INGREDIENTS_PACKET, RitualTableRecipe::ingredients,
                        ItemStack.PACKET_CODEC, RitualTableRecipe::output,
                        RitualTableRecipe::new
                );

        @Override
        public MapCodec<RitualTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, RitualTableRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}