package net.astralya.hexalia.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.compat.jei.category.RitualBrazierRecipeCategory;
import net.astralya.hexalia.compat.jei.category.SmallCauldronRecipeCategory;
import net.astralya.hexalia.compat.jei.category.RitualTableRecipeCategory;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.screen.SmallCauldronScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(HexaliaMod.MODID, "jei_plugin");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SmallCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RitualTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RitualBrazierRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SMALL_CAULDRON.get()), SmallCauldronRecipeCategory.SMALL_CAULDRON_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RITUAL_TABLE.get()), RitualTableRecipeCategory.RITUAL_TABLE_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.RITUAL_BRAZIER.get()), RitualBrazierRecipeCategory.RITUAL_BRAZIER_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<SmallCauldronRecipe> smallCauldronRecipes = recipeManager.getAllRecipesFor(SmallCauldronRecipe.Type.INSTANCE);
        registration.addRecipes(SmallCauldronRecipeCategory.SMALL_CAULDRON_TYPE, smallCauldronRecipes);

        List<RitualTableRecipe> transmutationRecipes = recipeManager.getAllRecipesFor(RitualTableRecipe.Type.INSTANCE);
        registration.addRecipes(RitualTableRecipeCategory.RITUAL_TABLE_RECIPE_RECIPE_TYPE, transmutationRecipes);

        List<RitualBrazierRecipe> ritualBrazierRecipes = recipeManager.getAllRecipesFor(RitualBrazierRecipe.Type.INSTANCE);
        registration.addRecipes(RitualBrazierRecipeCategory.RITUAL_BRAZIER_TYPE, ritualBrazierRecipes);

        registration.addIngredientInfo(List.of(new ItemStack(ModBlocks.WILD_SUNFIRE_TOMATO.get()), new ItemStack(ModItems.SUNFIRE_TOMATO.get()),
                new ItemStack(ModItems.SUNFIRE_TOMATO_SEEDS.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.info.wild_sunfire_tomatoes"));
        registration.addIngredientInfo(List.of(new ItemStack(ModBlocks.WILD_MANDRAKE.get()), new ItemStack(ModItems.MANDRAKE.get()), new ItemStack(ModItems.MANDRAKE_SEEDS.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.info.wild_mandrakes"));
        registration.addIngredientInfo(List.of(new ItemStack(ModBlocks.CHILLBERRY_BUSH.get()), new ItemStack(ModItems.CHILLBERRIES.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.info.chillberry_bushes"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.SALTSPROUT.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.info.saltsprout"));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(SmallCauldronScreen.class, 89, 25, 24, 17,
                SmallCauldronRecipeCategory.SMALL_CAULDRON_TYPE);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
