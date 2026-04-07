package net.astralya.hexalia.compat.rei.mutation;

import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class MutationDisplay extends BasicDisplay {

    @SuppressWarnings("unused")
    public MutationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public MutationDisplay(RecipeHolder<MutationRecipe> entry) {
        this(entry.value());
    }

    public MutationDisplay(MutationRecipe recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.inputItem())),
                List.of(EntryIngredients.of(recipe.output().copy()))
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MutationCategory.MUTATION;
    }
}