package net.grapes.hexalia.compat.rei.brazier;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.grapes.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RitualBrazierDisplay extends BasicDisplay {

    public RitualBrazierDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public RitualBrazierDisplay(RitualBrazierRecipe recipe) {
        super(getInputList(recipe), getOutputList(recipe));
    }

    private static List<EntryIngredient> getInputList(RitualBrazierRecipe recipe) {
        if (recipe == null) return Collections.emptyList();

        List<EntryIngredient> inputs = new ArrayList<>();

        Ingredient inputIngredient = recipe.getInput();
        for (ItemStack stack : inputIngredient.getMatchingStacks()) {
            inputs.add(EntryIngredient.of(EntryStacks.of(stack)));
        }

        return inputs;
    }

    private static List<EntryIngredient> getOutputList(RitualBrazierRecipe recipe) {
        if (recipe == null) return Collections.emptyList();

        List<EntryIngredient> outputs = new ArrayList<>();
        outputs.add(EntryIngredient.of(EntryStacks.of(recipe.getOutput(DynamicRegistryManager.EMPTY))));

        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RitualBrazierCategory.RITUAL_BRAZIER;
    }
}