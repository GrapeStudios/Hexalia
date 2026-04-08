package net.astralya.hexalia.compat.emi.small_cauldron;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public final class SmallCauldronEmiRecipe implements EmiRecipe {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/category/small_cauldron_gui.png");

    private final RecipeHolder<SmallCauldronRecipe> entry;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public SmallCauldronEmiRecipe(RecipeHolder<SmallCauldronRecipe> entry) {
        this.entry = entry;
        this.inputs = getInputs(entry.value());
        this.outputs = List.of(EmiStack.of(entry.value().getResultItem(null).copy()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return SmallCauldronEmiCategory.CATEGORY;
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
        return 89;
    }

    @Override
    public int getDisplayHeight() {
        return 42;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 89, 42, 14, 19);

        if (!this.inputs.isEmpty()) {
            widgets.addSlot(this.inputs.get(0), 0, 0)
                    .drawBack(false);
        }

        if (this.inputs.size() > 1) {
            widgets.addSlot(this.inputs.get(1), 24, 0)
                    .drawBack(false);
        }

        if (this.inputs.size() > 2) {
            widgets.addSlot(this.inputs.get(2), 0, 24)
                    .drawBack(false);
        }

        if (this.inputs.size() > 3) {
            widgets.addSlot(this.inputs.get(3), 24, 24)
                    .drawBack(false);
        }

        widgets.addSlot(this.outputs.get(0), 69, 11)
                .drawBack(false)
                .recipeContext(this);
    }

    private static List<EmiIngredient> getInputs(SmallCauldronRecipe recipe) {
        List<EmiIngredient> list = new ArrayList<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            list.add(EmiIngredient.of(ingredient));
        }

        return list;
    }
}