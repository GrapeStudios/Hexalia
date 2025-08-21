package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RitualBrazierProcessor implements IComponentProcessor {
    protected RitualBrazierRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        ResourceLocation recipeId = new ResourceLocation(variables.get("recipe").asString());
        recipe = level.getRecipeManager().byKey(recipeId)
                .filter(recipe -> recipe.getType().equals(RitualBrazierRecipe.Type.INSTANCE))
                .map(recipe -> (RitualBrazierRecipe) recipe)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (key.equals("output")) {
            return IVariable.from(recipe.getResultItem(level.registryAccess()));
        } else if (key.equals("header")) {
            return IVariable.from(recipe.getResultItem(level.registryAccess()).getHoverName());
        } else if (key.equals("input")) {
            ItemStack[] stack = recipe.getInput().getItems();
            return stack.length > 0 ? IVariable.from(stack[0]) : null;
        }
        return null;
    }
}
