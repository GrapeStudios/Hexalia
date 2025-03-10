package net.grapes.hexalia.compat.patchouli;

import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class TransmutationProcessor implements IComponentProcessor {
    protected TransmutationRecipe recipe;

    @Override
    public void setup(World level, IVariableProvider variables) {
        recipe = (TransmutationRecipe) level.getRecipeManager().get(new Identifier(variables.get("recipe").asString()))
                .filter(recipe -> recipe.getType().equals(TransmutationRecipe.Type.INSTANCE)).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public IVariable process(World level, String key) {
        if (key.equals("output")) {
            return IVariable.from(recipe.getOutput(level.getRegistryManager()));
        } else if (key.equals("header")) {
            return IVariable.wrap(recipe.getOutput(level.getRegistryManager()).getName().getString());
        } else if (key.equals("input")) {
            return IVariable.from(recipe.getInput());
        } else if (key.startsWith("salt_items")) {
            int index = Integer.parseInt(key.substring(10));
            if (index < recipe.getSaltItems().size()) {
                return IVariable.from(recipe.getSaltItems().get(index));
            }
        }
        return null;
    }
}