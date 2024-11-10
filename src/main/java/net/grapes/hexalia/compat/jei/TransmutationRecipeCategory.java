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
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TransmutationRecipeCategory implements IRecipeCategory<TransmutationRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(HexaliaMod.MOD_ID, "ritual_table");
    public static final ResourceLocation TEXTURE = new ResourceLocation(HexaliaMod.MOD_ID,
            "textures/gui/ritual_table_gui.png");

    public static final RecipeType<TransmutationRecipe> TRANSMUTATION_TYPE =
            new RecipeType<>(UID, TransmutationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public TransmutationRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0,118, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.RITUAL_TABLE.get()));
    }

    @Override
    public RecipeType<TransmutationRecipe> getRecipeType() {
        return TRANSMUTATION_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, TransmutationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 31).addItemStack(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 7).addItemStack(recipe.getSaltItems().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 55).addItemStack(recipe.getSaltItems().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 52, 31).addItemStack(recipe.getSaltItems().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 31).addItemStack(recipe.getSaltItems().get(3));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 31).addItemStack(recipe.getResultItem(null));
    }
}
