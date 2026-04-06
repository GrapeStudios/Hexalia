package net.astralya.hexalia.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public final class SmallCauldronRecipeInput implements RecipeInput {

    private final List<ItemStack> items;

    public SmallCauldronRecipeInput(List<ItemStack> stacks) {
        this.items = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            this.items.add(stack.copy());
        }
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public int getSize() {
        return items.size();
    }
}