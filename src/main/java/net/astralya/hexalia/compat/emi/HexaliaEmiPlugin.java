package net.astralya.hexalia.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.compat.emi.mutation.MutationEmiCategory;
import net.astralya.hexalia.compat.emi.mutation.MutationEmiRecipe;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.astralya.hexalia.compat.emi.mortar_and_pestle.MortarAndPestleEmiCategory;
import net.astralya.hexalia.compat.emi.mortar_and_pestle.MortarAndPestleEmiRecipe;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.compat.emi.ritual_brazier.RitualBrazierEmiCategory;
import net.astralya.hexalia.compat.emi.ritual_brazier.RitualBrazierEmiRecipe;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.compat.emi.ritual_table.RitualTableEmiCategory;
import net.astralya.hexalia.compat.emi.ritual_table.RitualTableEmiRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.compat.emi.small_cauldron.SmallCauldronEmiCategory;
import net.astralya.hexalia.compat.emi.small_cauldron.SmallCauldronEmiRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;

@EmiEntrypoint
public final class HexaliaEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(MutationEmiCategory.CATEGORY);
        registry.addWorkstation(MutationEmiCategory.CATEGORY, EmiStack.of(ModItems.MUTAVIS.get()));

        for (RecipeHolder<MutationRecipe> entry : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MUTATION_TYPE.get())) {
            registry.addRecipe(new MutationEmiRecipe(entry));
        }

        registry.addCategory(MortarAndPestleEmiCategory.CATEGORY);
        registry.addWorkstation(MortarAndPestleEmiCategory.CATEGORY, EmiStack.of(ModItems.MORTAR_AND_PESTLE.get()));

        for (RecipeHolder<MortarAndPestleRecipe> entry : registry.getRecipeManager().getAllRecipesFor(ModRecipes.MORTAR_AND_PESTLE_TYPE.get())) {
            registry.addRecipe(new MortarAndPestleEmiRecipe(entry));
        }

        registry.addCategory(RitualBrazierEmiCategory.CATEGORY);
        registry.addWorkstation(RitualBrazierEmiCategory.CATEGORY, EmiStack.of(ModBlocks.RITUAL_BRAZIER.get()));

        for (RecipeHolder<RitualBrazierRecipe> entry : registry.getRecipeManager().getAllRecipesFor(ModRecipes.RITUAL_BRAZIER_TYPE.get())) {
            registry.addRecipe(new RitualBrazierEmiRecipe(entry));
        }

        registry.addCategory(RitualTableEmiCategory.CATEGORY);
        registry.addWorkstation(RitualTableEmiCategory.CATEGORY, EmiStack.of(ModBlocks.RITUAL_TABLE.get()));

        for (RecipeHolder<RitualTableRecipe> entry : registry.getRecipeManager().getAllRecipesFor(ModRecipes.RITUAL_TABLE_TYPE.get())) {
            registry.addRecipe(new RitualTableEmiRecipe(entry));
        }

        registry.addCategory(SmallCauldronEmiCategory.CATEGORY);
        registry.addWorkstation(SmallCauldronEmiCategory.CATEGORY, EmiStack.of(ModItems.SMALL_CAULDRON.get()));

        for (RecipeHolder<SmallCauldronRecipe> entry : registry.getRecipeManager().getAllRecipesFor(ModRecipes.SMALL_CAULDRON_TYPE.get())) {
            registry.addRecipe(new SmallCauldronEmiRecipe(entry));
        }
    }
}