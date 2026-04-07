package net.astralya.hexalia.compat.rei.ritual_table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RitualTableDisplay extends BasicDisplay {

    @SuppressWarnings("unused")
    public RitualTableDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public RitualTableDisplay(RecipeHolder<RitualTableRecipe> entry) {
        this(entry.value());
    }

    public RitualTableDisplay(RitualTableRecipe recipe) {
        super(
                getInputList(recipe),
                List.of(EntryIngredients.of(recipe.output().copy()))
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RitualTableCategory.RITUAL_TABLE;
    }

    private static List<EntryIngredient> getInputList(RitualTableRecipe recipe) {
        if (recipe == null) {
            return Collections.emptyList();
        }

        List<EntryIngredient> list = new ArrayList<>();
        for (Ingredient ingredient : recipe.ingredients()) {
            list.add(EntryIngredients.ofIngredient(ingredient));
        }
        return list;
    }
}