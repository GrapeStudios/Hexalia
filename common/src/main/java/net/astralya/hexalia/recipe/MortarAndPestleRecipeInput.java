package net.astralya.hexalia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record MortarAndPestleRecipeInput(ItemStack first, ItemStack second, ItemStack third)
    implements RecipeInput {
  @Override
  public ItemStack getItem(int index) {
    return switch (index) {
      case 0 -> first;
      case 1 -> second;
      case 2 -> third;
      default -> ItemStack.EMPTY;
    };
  }

  @Override
  public int size() {
    return 3;
  }
}
