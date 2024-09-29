package net.grapes.hexalia.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualTableDisplay extends BasicDisplay {

    public RitualTableDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public RitualTableDisplay(TransmutationRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null)))));
    }

    private static List<EntryIngredient> getInputList(TransmutationRecipe recipe) {
        if (recipe == null) return Collections.emptyList();

        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredient.of(EntryStacks.of(recipe.getInput())));
        for (ItemStack saltItem : recipe.getSaltItems()) {
            list.add(EntryIngredient.of(EntryStacks.of(saltItem)));
        }

        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RitualTableCategory.RITUAL_TABLE;
    }
}
