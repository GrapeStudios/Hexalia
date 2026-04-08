package net.astralya.hexalia.compat.emi.mutation;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.RecipeEntry;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public final class MutationEmiRecipe implements EmiRecipe {

    public static final Identifier TEXTURE = Identifier.of(HexaliaMod.MODID, "textures/gui/mutation_gui.png");

    private final RecipeEntry<MutationRecipe> entry;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public MutationEmiRecipe(RecipeEntry<MutationRecipe> entry) {
        this.entry = entry;
        this.inputs = List.of(EmiIngredient.of(entry.value().inputItem()));
        this.outputs = List.of(EmiStack.of(entry.value().output().copy()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MutationEmiCategory.CATEGORY;
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
        return 80;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 80, 0, 0);

        widgets.addSlot(this.inputs.getFirst(), 47, 31)
                .drawBack(false);

        widgets.addSlot(this.outputs.getFirst(), 88, 30)
                .drawBack(false)
                .recipeContext(this);

        widgets.addTooltipText(
                List.of(Text.translatable("tooltip.hexalia.mutation")),
                4,
                55,
                16,
                16
        );
    }
}