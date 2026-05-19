package net.astralya.hexalia.compat.jei.util;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class JeiLayoutHelper {
  private JeiLayoutHelper() {}

  public static void addIngredientRow(
      IRecipeLayoutBuilder builder, List<Ingredient> ingredients, int x, int y, int spacing) {
    for (int index = 0; index < ingredients.size(); index++) {
      addInput(builder, ingredients.get(index), x + index * spacing, y);
    }
  }

  public static void addIngredientGrid(
      IRecipeLayoutBuilder builder,
      List<Ingredient> ingredients,
      int x,
      int y,
      int columns,
      int spacing) {
    for (int index = 0; index < ingredients.size(); index++) {
      int column = index % columns;
      int row = index / columns;
      addInput(builder, ingredients.get(index), x + column * spacing, y + row * spacing);
    }
  }

  public static IRecipeSlotBuilder addNamedInput(
      IRecipeLayoutBuilder builder, Ingredient ingredient, int x, int y, String name) {
    return addInput(builder, ingredient, x, y).setSlotName(name);
  }

  public static IRecipeSlotBuilder addInput(
      IRecipeLayoutBuilder builder, Ingredient ingredient, int x, int y) {
    return builder.addInputSlot(x + 1, y + 1).addIngredients(ingredient);
  }

  public static IRecipeSlotBuilder addOutput(
      IRecipeLayoutBuilder builder, ItemStack output, int x, int y) {
    return builder.addOutputSlot(x + 1, y + 1).addItemStack(output.copy());
  }

  public static void addIngredientSlots(
      IRecipeLayoutBuilder builder, List<Ingredient> ingredients, HexaliaRecipeGuiLayout layout) {
    for (int index = 0; index < ingredients.size(); index++) {
      addInput(builder, ingredients.get(index), layout.inputX(index), layout.inputY(index));
    }
  }

  public static IRecipeSlotBuilder addOutput(
      IRecipeLayoutBuilder builder, ItemStack output, HexaliaRecipeGuiLayout layout) {
    return addOutput(builder, output, layout.outputX(), layout.outputY());
  }
}
