package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

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
        Identifier id = Identifier.of(variables.get("recipe", level.getRegistryManager()).asString());
        List<RecipeEntry<RitualTableRecipe>> all = w.getRecipeManager().listAllOfType(ModRecipes.RITUAL_TABLE_TYPE);
        this.recipe = all.stream()
                .filter(e -> e.id().equals(id))
                .findFirst()
                .map(RecipeEntry::value)
                .orElseThrow(() -> new IllegalArgumentException("Ritual Table recipe not found: " + id));
    }

    @Override
    public IVariable process(World level, String key) {
        if (recipe == null) return null;
        World w = ctx(level);

        if ("output".equals(key)) {
            return IVariable.from(recipe.output().copy(), w.getRegistryManager());
        } else if ("header".equals(key)) {
            return IVariable.wrap(recipe.output().getName().getString());
        }

        if ("input_main".equals(key)) {
            if (!recipe.ingredients().isEmpty()) {
                Ingredient main = recipe.ingredients().get(0);
                ItemStack[] stacks = main.getMatchingStacks();
                return stacks.length > 0 ? IVariable.from(stacks[0].copy(), w.getRegistryManager()) : null;
            }
            return null;
        }

        if (key.startsWith("input_brazier")) {
            try {
                int idx = Integer.parseInt(key.substring("input_brazier".length()));
                int ingIndex = idx;
                if (ingIndex >= 1 && ingIndex < recipe.ingredients().size()) {
                    Ingredient ing = recipe.ingredients().get(ingIndex);
                    ItemStack[] stacks = ing.getMatchingStacks();
                    return stacks.length > 0 ? IVariable.from(stacks[0].copy(), w.getRegistryManager()) : null;
                }
            } catch (NumberFormatException ignored) {}
            return null;
        }

        return null;
    }
}