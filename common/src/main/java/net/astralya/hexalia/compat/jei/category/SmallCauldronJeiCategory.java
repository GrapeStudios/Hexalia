package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.astralya.hexalia.compat.jei.HexaliaJeiRecipeTypes;
import net.astralya.hexalia.compat.jei.util.JeiLayoutHelper;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class SmallCauldronJeiCategory
    extends AbstractHexaliaJeiCategory<SmallCauldronRecipe> {
  public SmallCauldronJeiCategory(IGuiHelper guiHelper) {
    super(
        guiHelper,
        HexaliaJeiRecipeTypes.SMALL_CAULDRON,
        "jei.hexalia.category.small_cauldron",
        ModItems.SMALL_CAULDRON.get(),
        HexaliaRecipeGuiLayout.SMALL_CAULDRON);
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder, SmallCauldronRecipe recipe, IFocusGroup focuses) {
    HexaliaRecipeGuiLayout layout = HexaliaRecipeGuiLayout.SMALL_CAULDRON;
    JeiLayoutHelper.addIngredientSlots(builder, recipe.getIngredients(), layout);
    JeiLayoutHelper.addOutput(builder, recipe.getResultItem(null), layout)
        .addRichTooltipCallback(
            (view, tooltip) -> {
              tooltip.add(
                  Component.translatable("jei.hexalia.tooltip.brew_time", recipe.getBrewTime())
                      .withStyle(ChatFormatting.GRAY));
              if (recipe.getExperience() > 0.0F) {
                tooltip.add(
                    Component.translatable("jei.hexalia.tooltip.experience", recipe.getExperience())
                        .withStyle(ChatFormatting.GRAY));
              }
            });
  }
}
