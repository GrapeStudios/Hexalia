package net.astralya.hexalia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public final class SmallCauldronRecipeInput implements RecipeInput {

    private final List<ItemStack> items;

    public SmallCauldronRecipeInput(List<ItemStack> stacks) {
        this.items = new ArrayList<>(stacks.size());
        for (ItemStack s : stacks) {
            this.items.add(s.copy());
        }
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
