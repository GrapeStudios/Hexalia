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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RitualTableRecipeCategory implements IRecipeCategory<RitualTableRecipe> {

    public static final Identifier UID = Identifier.of(HexaliaMod.MODID, "ritual_table");
    public static final Identifier TEXTURE = Identifier.of(HexaliaMod.MODID, "textures/gui/ritual_table_gui.png");

    public static final RecipeType<RitualTableRecipe> RITUAL_TABLE_RECIPE_TYPE =
            new RecipeType<>(UID, RitualTableRecipe.class);

    private static final int WIDTH = 118;
    private static final int HEIGHT = 80;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable hexIcon;

    public RitualTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RITUAL_TABLE));
        this.hexIcon = helper.createDrawable(TEXTURE, 0, 0, 16, 16);
    }

    @Override
    public @NotNull RecipeType<RitualTableRecipe> getRecipeType() {
        return RITUAL_TABLE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Text getTitle() {
        return Text.translatable("block.hexalia.ritual_table");
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
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(@NotNull RitualTableRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull DrawContext drawContext, double mouseX, double mouseY) {
        background.draw(drawContext, 0, 0);
        hexIcon.draw(drawContext, 4, 55);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, RitualTableRecipe recipe, @NotNull IFocusGroup focuses) {
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

        if (MinecraftClient.getInstance().world != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 31)
                    .addItemStack(recipe.getOutput(MinecraftClient.getInstance().world.getRegistryManager()));
        }
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull RitualTableRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 4 && mouseX < 20 && mouseY >= 55 && mouseY < 71) {
            tooltip.add(Text.translatable("tooltip.hexalia.hex_focus_gui"));
        }
    }
}