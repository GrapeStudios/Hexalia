package net.astralya.hexalia.recipe;


import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record RitualBrazierRecipeInput(ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        return input;
    }

    @Override
    public int getSize() {
        return 1;
    }
}