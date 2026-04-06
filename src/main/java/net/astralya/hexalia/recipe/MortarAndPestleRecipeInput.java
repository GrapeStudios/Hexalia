package net.astralya.hexalia.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record MortarAndPestleRecipeInput(ItemStack a, ItemStack b, ItemStack c) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int index) {
        return switch (index) {
            case 0 -> a;
            case 1 -> b;
            case 2 -> c;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSize() {
        return 3;
    }
}