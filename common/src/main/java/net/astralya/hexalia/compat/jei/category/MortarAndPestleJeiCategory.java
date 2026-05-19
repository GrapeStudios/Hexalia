package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.astralya.hexalia.compat.jei.HexaliaJeiRecipeTypes;
import net.astralya.hexalia.compat.jei.util.JeiLayoutHelper;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;

public final class MortarAndPestleJeiCategory
    extends AbstractHexaliaJeiCategory<MortarAndPestleRecipe> {
  public MortarAndPestleJeiCategory(IGuiHelper guiHelper) {
    super(
        guiHelper,
        HexaliaJeiRecipeTypes.MORTAR_AND_PESTLE,
        "jei.hexalia.category.mortar_and_pestle",
        ModItems.MORTAR_AND_PESTLE.get(),
        HexaliaRecipeGuiLayout.MORTAR_AND_PESTLE);
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder, MortarAndPestleRecipe recipe, IFocusGroup focuses) {
    HexaliaRecipeGuiLayout layout = HexaliaRecipeGuiLayout.MORTAR_AND_PESTLE;
    JeiLayoutHelper.addIngredientSlots(builder, recipe.ingredients(), layout);
    JeiLayoutHelper.addOutput(builder, recipe.output(), layout);
  }
}
