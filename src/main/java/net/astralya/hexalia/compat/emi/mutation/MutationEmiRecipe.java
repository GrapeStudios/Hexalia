package net.astralya.hexalia.compat.emi.mutation;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class MutationEmiRecipe implements EmiRecipe {

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/mutation_gui.png");

    private final RecipeHolder<MutationRecipe> entry;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public MutationEmiRecipe(RecipeHolder<MutationRecipe> entry) {
        this.entry = entry;
        this.inputs = List.of(EmiIngredient.of(entry.value().inputItem()));
        this.outputs = List.of(EmiStack.of(entry.value().output().copy()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MutationEmiCategory.CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
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

        widgets.addSlot(this.inputs.get(0), 47, 31)
                .drawBack(false);

        widgets.addSlot(this.outputs.get(0), 88, 30)
                .drawBack(false)
                .recipeContext(this);

        widgets.addTooltipText(
                List.of(Component.translatable("tooltip.hexalia.mutation")),
                4,
                55,
                16,
                16
        );
    }
}