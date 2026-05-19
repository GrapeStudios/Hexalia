package net.astralya.hexalia.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.astralya.hexalia.compat.jei.HexaliaJeiRecipeTypes;
import net.astralya.hexalia.compat.jei.util.JeiLayoutHelper;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class CelestialInfusionJeiCategory
    extends AbstractHexaliaJeiCategory<CelestialInfusionRecipe> {
  public CelestialInfusionJeiCategory(IGuiHelper guiHelper) {
    super(
        guiHelper,
        HexaliaJeiRecipeTypes.CELESTIAL_INFUSION,
        "jei.hexalia.category.celestial_infusion",
        ModItems.RITUAL_BRAZIER.get(),
        HexaliaRecipeGuiLayout.CELESTIAL_INFUSION);
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder, CelestialInfusionRecipe recipe, IFocusGroup focuses) {
    HexaliaRecipeGuiLayout layout = HexaliaRecipeGuiLayout.CELESTIAL_INFUSION;
    JeiLayoutHelper.addInput(builder, recipe.inputItem(), layout.inputX(0), layout.inputY(0));
    JeiLayoutHelper.addOutput(builder, recipe.output(), layout);
  }

  @Override
  public void getTooltip(
      ITooltipBuilder tooltipBuilder,
      CelestialInfusionRecipe recipe,
      IRecipeSlotsView recipeSlotsView,
      double mouseX,
      double mouseY) {
    if (mouseX >= 0 && mouseX <= getWidth() && mouseY >= 0 && mouseY <= getHeight()) {
      tooltipBuilder.addAll(
          List.of(
              Component.translatable("jei.hexalia.tooltip.requires_hex_focus")
                  .withStyle(ChatFormatting.GRAY),
              Component.translatable("jei.hexalia.tooltip.requires_salted_brazier")
                  .withStyle(ChatFormatting.GRAY),
              Component.translatable("jei.hexalia.tooltip.requires_celestial_blooms")
                  .withStyle(ChatFormatting.GRAY),
              Component.translatable("jei.hexalia.tooltip.requires_open_sky")
                  .withStyle(ChatFormatting.GRAY)));
    }
  }
}
