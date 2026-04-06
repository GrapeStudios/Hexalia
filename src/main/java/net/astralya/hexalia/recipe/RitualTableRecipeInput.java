package net.astralya.hexalia.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record RitualTableRecipeInput(Container inventory) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return inventory.getItem(index);
    }
    @Override
    public int size() {
        return inventory.getContainerSize();
    }
}
