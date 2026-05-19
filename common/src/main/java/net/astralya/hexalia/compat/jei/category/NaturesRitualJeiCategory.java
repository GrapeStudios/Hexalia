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
import net.astralya.hexalia.recipe.NaturesRitualRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

public final class NaturesRitualJeiCategory
    extends AbstractHexaliaJeiCategory<NaturesRitualRecipe> {
  public NaturesRitualJeiCategory(IGuiHelper guiHelper) {
    super(
        guiHelper,
        HexaliaJeiRecipeTypes.NATURES_RITUAL,
        "jei.hexalia.category.natures_ritual",
        ModItems.RITUAL_TABLE.get(),
        HexaliaRecipeGuiLayout.NATURES_RITUAL);
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder, NaturesRitualRecipe recipe, IFocusGroup focuses) {
    List<Ingredient> ingredients = recipe.ingredients();
    HexaliaRecipeGuiLayout layout = HexaliaRecipeGuiLayout.NATURES_RITUAL;
    if (!ingredients.isEmpty()) {
      JeiLayoutHelper.addNamedInput(
          builder, ingredients.get(0), layout.inputX(0), layout.inputY(0), "ritual_table");
    }

    for (int index = 1; index < ingredients.size(); index++) {
      JeiLayoutHelper.addNamedInput(
          builder,
          ingredients.get(index),
          layout.inputX(index),
          layout.inputY(index),
          "ritual_brazier_" + index);
    }
    JeiLayoutHelper.addOutput(builder, recipe.output(), layout);
  }

  @Override
  public void getTooltip(
      ITooltipBuilder tooltipBuilder,
      NaturesRitualRecipe recipe,
      IRecipeSlotsView recipeSlotsView,
      double mouseX,
      double mouseY) {
    if (mouseX >= 0 && mouseX <= getWidth() && mouseY >= 0 && mouseY <= getHeight()) {
      tooltipBuilder.addAll(
          List.of(
              Component.translatable("jei.hexalia.tooltip.requires_hex_focus")
                  .withStyle(ChatFormatting.GRAY),
              Component.translatable("jei.hexalia.tooltip.requires_salted_braziers")
                  .withStyle(ChatFormatting.GRAY),
              Component.translatable("jei.hexalia.tooltip.requires_mature_crops")
                  .withStyle(ChatFormatting.GRAY)));
    }
  }
}
