package net.astralya.hexalia.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public final class SmallCauldronRecipeInput implements RecipeInput {
  private final List<ItemStack> items;

  public SmallCauldronRecipeInput(List<ItemStack> stacks) {
    items = new ArrayList<>(stacks.size());
    for (ItemStack stack : stacks) {
      items.add(stack.copy());
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
