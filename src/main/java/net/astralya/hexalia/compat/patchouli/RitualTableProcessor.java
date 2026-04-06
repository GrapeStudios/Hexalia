package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.core.HolderLookup;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;
import java.util.Optional;

public class RitualTableProcessor implements IComponentProcessor {

    private RitualTableRecipe recipe;

    private static HolderLookup.Provider regs(Level level) {
        Level ctx = level != null ? level : Minecraft.getInstance().level;
        if (ctx == null) throw new IllegalStateException("No level available for RitualTableProcessor");
        return ctx.registryAccess();
    }

    private static Level ctx(Level level) {
        return level != null ? level : Minecraft.getInstance().level;
    }

    @Override
    public void setup(Level level, IVariableProvider variables) {
        HolderLookup.Provider reg = regs(level);
        String recipeIdStr = variables.get("recipe", reg).asString();
        ResourceLocation recipeId = ResourceLocation.parse(recipeIdStr);

        Level world = ctx(level);
        List<RecipeHolder<RitualTableRecipe>> all =
                world.getRecipeManager().getAllRecipesFor(ModRecipes.RITUAL_TABLE_TYPE.get());

        Optional<RitualTableRecipe> match = all.stream()
                .filter(r -> r.id().equals(recipeId))
                .map(RecipeHolder::value)
                .findFirst();

        this.recipe = match.orElseThrow(() ->
                new IllegalArgumentException("Ritual Table recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) return null;

        HolderLookup.Provider reg = regs(level);

        if (key.equals("output")) {
            return IVariable.from(recipe.getResultItem(reg), reg);
        }
        if (key.equals("header")) {
            return IVariable.from(recipe.getResultItem(reg).getHoverName(), reg);
        }
        if (key.equals("input_main")) {
            if (!recipe.getIngredients().isEmpty()) {
                Ingredient main = recipe.getIngredients().getFirst();
                ItemStack[] stacks = main.getItems();
                ItemStack stack = stacks.length > 0 ? stacks[0] : ItemStack.EMPTY;
                return IVariable.from(stack, reg);
            }
            return IVariable.from(ItemStack.EMPTY, reg);
        }
        if (key.startsWith("input_brazier")) {
            try {
                int idx = Integer.parseInt(key.substring("input_brazier".length()));
                int ingIndex = idx;
                if (ingIndex >= 1 && ingIndex < recipe.getIngredients().size()) {
                    Ingredient ing = recipe.getIngredients().get(ingIndex);
                    ItemStack[] stacks = ing.getItems();
                    ItemStack stack = stacks.length > 0 ? stacks[0] : ItemStack.EMPTY;
                    return IVariable.from(stack, reg);
                } else {
                    return IVariable.from(ItemStack.EMPTY, reg);
                }
            } catch (NumberFormatException e) {
                return IVariable.from(ItemStack.EMPTY, reg);
            }
        }

        return null;
    }
}