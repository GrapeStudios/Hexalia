package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class SmallCauldronProcessor implements IComponentProcessor {
    protected SmallCauldronRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        ResourceLocation recipeId = new ResourceLocation(variables.get("recipe").asString());
        recipe = level.getRecipeManager().byKey(recipeId)
                .filter(recipe -> recipe.getType().equals(SmallCauldronRecipe.Type.INSTANCE))
                .map(recipe -> (SmallCauldronRecipe) recipe)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (key.equals("output")) {
            return IVariable.from(recipe.getResultItem(level.registryAccess()));
        } else if (key.equals("header")) {
            return IVariable.from(recipe.getResultItem(level.registryAccess()).getHoverName());
        }
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (key.equals("ingredients" + i)) {
                ItemStack[] stack = recipe.getIngredients().get(i).getItems();
                return stack.length > 0 ? IVariable.from(stack[0]) : null;
            }
        }
        return null;
    }

}
