package net.grapes.hexalia.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SmallCauldronRecipeCategory implements IRecipeCategory<SmallCauldronRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(HexaliaMod.MOD_ID, "small_cauldron");
    public static final ResourceLocation TEXTURE = new ResourceLocation(HexaliaMod.MOD_ID,
            "textures/gui/small_cauldron_category_gui.png");

    public static final RecipeType<SmallCauldronRecipe> SMALL_CAULDRON_TYPE =
            new RecipeType<>(UID, SmallCauldronRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SmallCauldronRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0,176, 81);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.SMALL_CAULDRON.get()));
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
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 27).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 27).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 66, 27).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 48).addIngredients(recipe.getBottleSlot());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 28).addItemStack(recipe.getResultItem(null));
    }
}
