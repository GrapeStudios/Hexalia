package net.astralya.hexalia.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.compat.rei.mortar_and_pestle.MortarAndPestleCategory;
import net.astralya.hexalia.compat.rei.mortar_and_pestle.MortarAndPestleDisplay;
import net.astralya.hexalia.compat.rei.mutation.MutationCategory;
import net.astralya.hexalia.compat.rei.mutation.MutationDisplay;
import net.astralya.hexalia.compat.rei.ritual_brazier.RitualBrazierCategory;
import net.astralya.hexalia.compat.rei.ritual_brazier.RitualBrazierDisplay;
import net.astralya.hexalia.compat.rei.ritual_table.RitualTableCategory;
import net.astralya.hexalia.compat.rei.ritual_table.RitualTableDisplay;
import net.astralya.hexalia.compat.rei.small_cauldron.SmallCauldronCategory;
import net.astralya.hexalia.compat.rei.small_cauldron.SmallCauldronDisplay;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.*;

@REIPluginClient
public class HexaliaREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MortarAndPestleCategory());
        registry.add(new MutationCategory());
        registry.add(new RitualBrazierCategory());
        registry.add(new RitualTableCategory());
        registry.add(new SmallCauldronCategory());

        registry.addWorkstations(
                MortarAndPestleCategory.MORTAR_AND_PESTLE,
                EntryStacks.of(ModBlocks.MORTAR_AND_PESTLE.get())
        );
        registry.addWorkstations(
                MutationCategory.MUTATION,
                EntryStacks.of(ModItems.MUTAVIS.get())
        );
        registry.addWorkstations(
                RitualBrazierCategory.RITUAL_BRAZIER,
                EntryStacks.of(ModBlocks.RITUAL_BRAZIER.get())
        );
        registry.addWorkstations(
                RitualTableCategory.RITUAL_TABLE,
                EntryStacks.of(ModBlocks.RITUAL_TABLE.get())
        );
        registry.addWorkstations(
                SmallCauldronCategory.SMALL_CAULDRON,
                EntryStacks.of(ModBlocks.SMALL_CAULDRON.get())
        );
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
                MortarAndPestleRecipe.class,
                ModRecipes.MORTAR_AND_PESTLE_TYPE.get(),
                MortarAndPestleDisplay::new
        );
        registry.registerRecipeFiller(
                MutationRecipe.class,
                ModRecipes.MUTATION_TYPE.get(),
                MutationDisplay::new
        );
        registry.registerRecipeFiller(
                RitualBrazierRecipe.class,
                ModRecipes.RITUAL_BRAZIER_TYPE.get(),
                RitualBrazierDisplay::new
        );
        registry.registerRecipeFiller(
                RitualTableRecipe.class,
                ModRecipes.RITUAL_TABLE_TYPE.get(),
                RitualTableDisplay::new
        );
        registry.registerRecipeFiller(
                SmallCauldronRecipe.class,
                ModRecipes.SMALL_CAULDRON_TYPE.get(),
                SmallCauldronDisplay::new
        );
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
    }

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }
}