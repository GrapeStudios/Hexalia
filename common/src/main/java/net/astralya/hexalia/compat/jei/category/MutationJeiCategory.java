package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.astralya.hexalia.compat.jei.HexaliaJeiRecipeTypes;
import net.astralya.hexalia.compat.jei.util.JeiLayoutHelper;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.MutationRecipe;

public final class MutationJeiCategory extends AbstractHexaliaJeiCategory<MutationRecipe> {
  public MutationJeiCategory(IGuiHelper guiHelper) {
    super(
        guiHelper,
        HexaliaJeiRecipeTypes.MUTATION,
        "jei.hexalia.category.mutation",
        ModItems.MUTAVIS.get(),
        HexaliaRecipeGuiLayout.MUTATION);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MutationRecipe recipe, IFocusGroup focuses) {
    HexaliaRecipeGuiLayout layout = HexaliaRecipeGuiLayout.MUTATION;
    JeiLayoutHelper.addInput(builder, recipe.inputItem(), layout.inputX(0), layout.inputY(0));
    JeiLayoutHelper.addOutput(builder, recipe.output(), layout);
  }
}
