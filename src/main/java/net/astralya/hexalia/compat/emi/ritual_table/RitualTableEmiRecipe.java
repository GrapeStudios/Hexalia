package net.astralya.hexalia.compat.emi.ritual_table;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class RitualTableEmiRecipe implements EmiRecipe {

    public static final Identifier TEXTURE = Identifier.of(HexaliaMod.MODID, "textures/gui/ritual_table_gui.png");

    private final RecipeEntry<RitualTableRecipe> entry;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public RitualTableEmiRecipe(RecipeEntry<RitualTableRecipe> entry) {
        this.entry = entry;
        this.inputs = getInputs(entry.value());
        this.outputs = List.of(EmiStack.of(entry.value().output().copy()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return RitualTableEmiCategory.CATEGORY;
    }

    @Override
    public Identifier getId() {
        return this.entry.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 88;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 80, 0, 0);

        if (!this.inputs.isEmpty()) {
            widgets.addSlot(this.inputs.get(0), 27, 30)
                    .drawBack(false);
        }

        if (this.inputs.size() > 1) {
            widgets.addSlot(this.inputs.get(1), 3, 30)
                    .drawBack(false);
        }

        if (this.inputs.size() > 2) {
            widgets.addSlot(this.inputs.get(2), 51, 30)
                    .drawBack(false);
        }

        if (this.inputs.size() > 3) {
            widgets.addSlot(this.inputs.get(3), 27, 6)
                    .drawBack(false);
        }

        if (this.inputs.size() > 4) {
            widgets.addSlot(this.inputs.get(4), 27, 54)
                    .drawBack(false);

            widgets.addTooltipText(
                    List.of(Text.translatable("tooltip.hexalia.hex_focus_gui")),
                    3,
                    62,
                    16,
                    16
            );
        }

        widgets.addSlot(this.outputs.get(0), 88, 30)
                .drawBack(false)
                .recipeContext(this);
    }

    private static List<EmiIngredient> getInputs(RitualTableRecipe recipe) {
        List<EmiIngredient> list = new ArrayList<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            list.add(EmiIngredient.of(ingredient));
        }

        return list;
    }
}