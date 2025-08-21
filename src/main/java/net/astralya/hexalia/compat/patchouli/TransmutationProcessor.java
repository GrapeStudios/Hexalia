package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.TransmutationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class TransmutationProcessor implements IComponentProcessor {
    protected TransmutationRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        ResourceLocation recipeId = new ResourceLocation(variables.get("recipe").asString());
        recipe = level.getRecipeManager().byKey(recipeId)
                .filter(recipe -> recipe.getType().equals(TransmutationRecipe.Type.INSTANCE))
                .map(recipe -> (TransmutationRecipe) recipe)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (key.equals("output")) {
            ItemStack output = recipe.getResultItem(level.registryAccess());
            if (!output.isEmpty()) {
                return IVariable.wrap(BuiltInRegistries.ITEM.getKey(output.getItem()).toString());
            }
        } else if (key.equals("header")) {
            ItemStack output = recipe.getResultItem(level.registryAccess());
            if (!output.isEmpty()) {
                return IVariable.wrap(output.getHoverName().getString());
            }
        } else if (key.equals("input")) {
                ItemStack inputItem = recipe.getInput();
                if (!inputItem.isEmpty()) {
                    return IVariable.wrap(BuiltInRegistries.ITEM.getKey(inputItem.getItem()).toString());
            }
        } else if (key.startsWith("salt_items")) {
            int index = Integer.parseInt(key.substring(10));
            if (index < recipe.getSaltItems().size()) {
                ItemStack saltItem = recipe.getSaltItems().get(index);
                if (!saltItem.isEmpty()) {
                    return IVariable.wrap(BuiltInRegistries.ITEM.getKey(saltItem.getItem()).toString());
                }
            }
        }
        return null;
    }
}
