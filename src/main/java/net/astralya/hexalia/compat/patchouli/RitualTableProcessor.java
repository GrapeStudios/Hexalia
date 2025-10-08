package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RitualTableProcessor implements IComponentProcessor {

    protected RitualTableRecipe recipe;

    private static World ctx(World world) {
        World w = world != null ? world : MinecraftClient.getInstance().world;
        if (w == null) throw new IllegalStateException("No world available for RitualTableProcessor");
        return w;
    }

    @Override
    public void setup(World level, IVariableProvider variables) {
        World w = ctx(level);
        Identifier id = new Identifier(variables.get("recipe").asString());
        this.recipe = w.getRecipeManager()
                .get(id)
                .filter(r -> r.getType().equals(ModRecipes.RITUAL_TABLE_TYPE))
                .map(r -> (RitualTableRecipe) r)
                .orElseThrow(() -> new IllegalArgumentException("Ritual Table recipe not found: " + id));
    }

    @Override
    public IVariable process(World level, String key) {
        if (recipe == null) return null;
        World w = ctx(level);

        if ("output".equals(key)) {
            return IVariable.from(recipe.getOutput(w.getRegistryManager()));
        } else if ("header".equals(key)) {
            return IVariable.wrap(recipe.getOutput(w.getRegistryManager()).getName().getString());
        }

        if ("input_main".equals(key)) {
            if (!recipe.getIngredients().isEmpty()) {
                Ingredient main = recipe.getIngredients().get(0);
                ItemStack[] stacks = main.getMatchingStacks();
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
                    ItemStack[] stacks = ing.getMatchingStacks();
                    return stacks.length > 0 ? IVariable.from(stacks[0]) : null;
                }
            } catch (NumberFormatException ignored) {}
            return null;
        }

        return null;
    }
}
