package net.astralya.hexalia.compat.rei.mutation;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;

public final class MutationDisplay extends BasicDisplay {

    public MutationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public MutationDisplay(RecipeEntry<MutationRecipe> entry) {
        this(entry.value());
    }

    public MutationDisplay(MutationRecipe recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.inputItem())),
                List.of(EntryIngredient.of(EntryStacks.of(recipe.output().copy())))
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MutationCategory.MUTATION;
    }
}