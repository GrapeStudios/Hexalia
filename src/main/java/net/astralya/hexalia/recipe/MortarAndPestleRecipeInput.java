package net.astralya.hexalia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record MortarAndPestleRecipeInput(ItemStack a, ItemStack b, ItemStack c) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> a;
            case 1 -> b;
            case 2 -> c;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 3;
    }
}