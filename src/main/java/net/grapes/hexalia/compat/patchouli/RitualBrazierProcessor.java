package net.grapes.hexalia.compat.patchouli;

import net.grapes.hexalia.recipe.RitualBrazierRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RitualBrazierProcessor implements IComponentProcessor {
    protected RitualBrazierRecipe recipe;

    @Override
    public void setup(World level, IVariableProvider variables) {
        recipe = (RitualBrazierRecipe) level.getRecipeManager().get(new Identifier(variables.get("recipe").asString()))
                .filter(recipe -> recipe.getType().equals(RitualBrazierRecipe.Type.INSTANCE))
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public IVariable process(World level, String key) {
        switch (key) {
            case "output":
                return IVariable.from(recipe.getOutput(level.getRegistryManager()));
            case "header":
                return IVariable.from(recipe.getOutput(level.getRegistryManager()).getName());
            case "input":
                ItemStack[] inputStacks = recipe.getInput().getMatchingStacks();
                return inputStacks.length > 0 ? IVariable.from(inputStacks[0]) : null;
            default:
                return null;
        }
    }
}