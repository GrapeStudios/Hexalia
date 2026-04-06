package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class SmallCauldronProcessor implements IComponentProcessor {

    private SmallCauldronRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String recipeIdStr = variables.get("recipe", level.registryAccess()).asString();
        ResourceLocation recipeId = ResourceLocation.parse(recipeIdStr);

        List<RecipeHolder<SmallCauldronRecipe>> allRecipes =
                Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipes.SMALL_CAULDRON_TYPE.get());

        this.recipe = allRecipes.stream()
                .filter(holder -> holder.id().equals(recipeId))
                .findFirst()
                .map(RecipeHolder::value)
                .orElseThrow(() -> new IllegalArgumentException("Small Cauldron recipe not found: " + recipeId));
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) return null;

        switch (key) {
            case "output":
                return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());
            case "header":
                return IVariable.from(recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());
        }

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (key.equals("ingredients" + i)) {
                ItemStack[] stacks = recipe.getIngredients().get(i).getItems();
                return stacks.length > 0 ? IVariable.from(stacks[0], level.registryAccess()) : null;
            }
        }
        return null;
    }
}