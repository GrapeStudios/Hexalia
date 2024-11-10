package net.grapes.hexalia.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.recipe.SmallCauldronRecipe;
import net.grapes.hexalia.recipe.TransmutationRecipe;
import net.grapes.hexalia.screen.SmallCauldronScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JeiHexaliaPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(HexaliaMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SmallCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TransmutationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<SmallCauldronRecipe> smallCauldronRecipes = recipeManager.getAllRecipesFor(SmallCauldronRecipe.Type.INSTANCE);
        registration.addRecipes(SmallCauldronRecipeCategory.SMALL_CAULDRON_TYPE, smallCauldronRecipes);

        List<TransmutationRecipe> transmutationRecipes = recipeManager.getAllRecipesFor(TransmutationRecipe.Type.INSTANCE);
        registration.addRecipes(TransmutationRecipeCategory.TRANSMUTATION_TYPE, transmutationRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(SmallCauldronScreen.class, 89, 25, 24, 17,
                SmallCauldronRecipeCategory.SMALL_CAULDRON_TYPE);
    }
}
