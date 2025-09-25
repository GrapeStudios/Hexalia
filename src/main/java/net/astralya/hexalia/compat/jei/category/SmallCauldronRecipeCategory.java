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
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmallCauldronRecipeCategory implements IRecipeCategory<SmallCauldronRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(HexaliaMod.MODID, "small_cauldron");
    public static final ResourceLocation TEXTURE = new ResourceLocation(HexaliaMod.MODID,
            "textures/gui/small_cauldron_category_gui.png");

    public static final RecipeType<SmallCauldronRecipe> SMALL_CAULDRON_TYPE =
            new RecipeType<>(UID, SmallCauldronRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable heatIcon;


    public SmallCauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0,118, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.SMALL_CAULDRON.get()));
        this.heatIcon = helper.createDrawable(TEXTURE, 0, 0,16, 16);
    }

    @Override
    public RecipeType<SmallCauldronRecipe> getRecipeType() {
        return SMALL_CAULDRON_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hexalia.small_cauldron");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SmallCauldronRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 31).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 31).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 52, 31).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 55).addIngredients(recipe.getBottleSlot());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 31).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(SmallCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        heatIcon.draw(guiGraphics, 28, 7);
    }

    @Override
    public List<Component> getTooltipStrings(SmallCauldronRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 28 && mouseX < 44 && mouseY >= 7 && mouseY < 23) {
            return List.of(Component.translatable("tooltip.hexalia.heat"));
        }
        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }
}
