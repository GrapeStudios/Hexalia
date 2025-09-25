package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RitualTableRecipeCategory implements IRecipeCategory<RitualTableRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(HexaliaMod.MODID, "ritual_table");
    public static final ResourceLocation TEXTURE = new ResourceLocation(HexaliaMod.MODID,
            "textures/gui/ritual_table_gui.png");

    public static final RecipeType<RitualTableRecipe> RITUAL_TABLE_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, RitualTableRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable hexIcon;

    public RitualTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0,118, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.RITUAL_TABLE.get()));
        this.hexIcon = helper.createDrawable(TEXTURE, 0, 0,16, 16);
    }

    @Override
    public RecipeType<RitualTableRecipe> getRecipeType() {
        return RITUAL_TABLE_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hexalia.ritual_table");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 31).addItemStack(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 7).addItemStack(recipe.getSaltItems().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 55).addItemStack(recipe.getSaltItems().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 52, 31).addItemStack(recipe.getSaltItems().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 31).addItemStack(recipe.getSaltItems().get(3));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 31).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(RitualTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        hexIcon.draw(guiGraphics, 4, 55);
    }

    @Override
    public List<Component> getTooltipStrings(RitualTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 4 && mouseX < 20 && mouseY >= 55 && mouseY < 71) {
            return List.of(Component.translatable("tooltip.hexalia.hex_focus_gui"));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }
}
