package net.grapes.hexalia.compat.patchouli;

import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class SmallCauldronProcessor implements IComponentProcessor {
    protected SmallCauldronRecipe recipe;

    @Override
    public void setup(World level, IVariableProvider variables) {
        recipe = (SmallCauldronRecipe) level.getRecipeManager().get(new Identifier(variables.get("recipe").asString()))
                .filter(recipe -> recipe.getType().equals(SmallCauldronRecipe.Type.INSTANCE)).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public IVariable process(World level, String key) {
        if (key.equals("output")) {
            return IVariable.from(recipe.getOutput(level.getRegistryManager()));
        } else if (key.equals("header")) {
            return IVariable.from(recipe.getOutput(level.getRegistryManager()).getName());
        }
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (key.equals("ingredients" + i)) {
                ItemStack[] stack = recipe.getIngredients().get(i).getMatchingStacks();
                return stack.length > 0 ? IVariable.from(stack[0]) : null;
            }

        }
        return null;
    }
}
