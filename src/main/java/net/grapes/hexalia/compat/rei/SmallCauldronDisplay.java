package net.grapes.hexalia.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmallCauldronDisplay extends BasicDisplay {
    private final EntryIngredient bottleSlot;

    public SmallCauldronDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, EntryIngredient bottleSlot) {
        super(inputs, outputs);
        this.bottleSlot = bottleSlot;
    }

    public SmallCauldronDisplay(SmallCauldronRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null)))));
        this.bottleSlot = EntryIngredients.ofIngredient(recipe.getBottleSlot());
    }

    private static List<EntryIngredient> getInputList(SmallCauldronRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            list.add(EntryIngredients.ofIngredient(ingredient));
        }
        list.add(EntryIngredients.ofIngredient(recipe.getBottleSlot()));
        return list;
    }

    public EntryIngredient getBottleSlot() {
        return bottleSlot;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SmallCauldronCategory.SMALL_CAULDRON;
    }
}
