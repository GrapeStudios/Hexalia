package net.astralya.hexalia.compat.rei.ritual_brazier;

import java.util.Collections;
import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RitualBrazierDisplay extends BasicDisplay {

    @SuppressWarnings("unused")
    public RitualBrazierDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public RitualBrazierDisplay(RecipeHolder<RitualBrazierRecipe> entry) {
        this(entry.value());
    }

    public RitualBrazierDisplay(RitualBrazierRecipe recipe) {
        super(getInputList(recipe), getOutputList(recipe));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RitualBrazierCategory.RITUAL_BRAZIER;
    }

    private static List<EntryIngredient> getInputList(RitualBrazierRecipe recipe) {
        if (recipe == null) {
            return Collections.emptyList();
        }

        return List.of(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
    }

    private static List<EntryIngredient> getOutputList(RitualBrazierRecipe recipe) {
        if (recipe == null) {
            return Collections.emptyList();
        }

        return List.of(EntryIngredients.of(recipe.getResultItem(null).copy()));
    }
}