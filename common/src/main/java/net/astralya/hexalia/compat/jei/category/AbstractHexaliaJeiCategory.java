package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

abstract class AbstractHexaliaJeiCategory<T> implements IRecipeCategory<T> {
  private final RecipeType<T> recipeType;
  private final Component title;
  private final IDrawable icon;
  private final HexaliaRecipeGuiLayout layout;

  protected AbstractHexaliaJeiCategory(
      IGuiHelper guiHelper,
      RecipeType<T> recipeType,
      String titleKey,
      ItemLike iconItem,
      HexaliaRecipeGuiLayout layout) {
    this.recipeType = recipeType;
    this.title = Component.translatable(titleKey);
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
    this.layout = layout;
  }

  @Override
  public RecipeType<T> getRecipeType() {
    return recipeType;
  }

  @Override
  public Component getTitle() {
    return title;
  }

  @Override
  public int getWidth() {
    return layout.width();
  }

  @Override
  public int getHeight() {
    return layout.height();
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public void draw(
      T recipe,
      mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
      GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    guiGraphics.blit(
        layout.texture(),
        0,
        0,
        layout.textureU(),
        layout.textureV(),
        layout.textureWidth(),
        layout.textureHeight(),
        256,
        256);
  }

  @Override
  public boolean needsRecipeBorder() {
    return false;
  }
}
