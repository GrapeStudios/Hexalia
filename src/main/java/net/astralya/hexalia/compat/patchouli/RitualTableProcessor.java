package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RitualTableProcessor implements IComponentProcessor {

    private RitualTableRecipe recipe;

    private static Level ctx(Level level) {
        Level world = level != null ? level : Minecraft.getInstance().level;
        if (world == null) throw new IllegalStateException("No level available for RitualTableProcessor");
        return world;
    }

    @Override
    public void setup(Level level, IVariableProvider variables) {
        Level world = ctx(level);

        ResourceLocation recipeId = new ResourceLocation(variables.get("recipe").asString());
        this.recipe = world.getRecipeManager().byKey(recipeId)
                .filter(r -> r.getType().equals(RitualTableRecipe.Type.INSTANCE)) // <- match your recipe type
                .map(r -> (RitualTableRecipe) r)
                .orElseThrow(() -> new IllegalArgumentException("Ritual Table recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) return null;

        if ("output".equals(key)) {
            return IVariable.from(recipe.getResultItem(ctx(level).registryAccess()));
        } else if ("header".equals(key)) {
            return IVariable.from(recipe.getResultItem(ctx(level).registryAccess()).getHoverName());
        }

        if ("input_main".equals(key)) {
            if (!recipe.getIngredients().isEmpty()) {
                Ingredient main = recipe.getIngredients().get(0);
                ItemStack[] stacks = main.getItems();
                return stacks.length > 0 ? IVariable.from(stacks[0]) : null;
            }
            return null;
        }

        if (key.startsWith("input_brazier")) {
            try {
                int idx = Integer.parseInt(key.substring("input_brazier".length()));
                int ingIndex = idx;
                if (ingIndex >= 1 && ingIndex < recipe.getIngredients().size()) {
                    Ingredient ing = recipe.getIngredients().get(ingIndex);
                    ItemStack[] stacks = ing.getItems();
                    return stacks.length > 0 ? IVariable.from(stacks[0]) : null;
                }
            } catch (NumberFormatException ignored) {}
            return null;
        }

        return null;
    }
}