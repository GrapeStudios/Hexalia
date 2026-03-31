package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RitualTableRecipeCategory implements IRecipeCategory<RitualTableRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "ritual_table");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID,
            "textures/gui/ritual_table_gui.png");

    public static final RecipeType<RitualTableRecipe> RITUAL_TABLE_RECIPE_TYPE =
            new RecipeType<>(UID, RitualTableRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable hexIcon;

    private static final int WIDTH = 118;
    private static final int HEIGHT = 80;

    public RitualTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.RITUAL_TABLE.get()));
        this.hexIcon = helper.createDrawable(TEXTURE, 0, 0, 16, 16);
    }

    @Override
    public RecipeType<RitualTableRecipe> getRecipeType() {
        return RITUAL_TABLE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hexalia.ritual_table");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(RitualTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        hexIcon.draw(guiGraphics, 4, 55);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualTableRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getIngredients();

        if (!ingredients.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 28, 31)
                    .addIngredients(ingredients.get(0));
        }

        int[] xPositions = {28, 28, 52, 4};
        int[] yPositions = {7, 55, 31, 31};

        for (int i = 1; i < ingredients.size() && i <= 4; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, xPositions[i - 1], yPositions[i - 1])
                    .addIngredients(ingredients.get(i));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 31)
                .addItemStack(recipe.getResultItem(null));
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, RitualTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 4 && mouseX < 20 && mouseY >= 55 && mouseY < 71) {
            tooltip.add(Component.translatable("tooltip.hexalia.hex_focus_gui"));
        }
    }
}