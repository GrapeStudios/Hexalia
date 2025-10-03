package net.astralya.hexalia.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.compat.rei.brewing.SmallCauldronCategory;
import net.astralya.hexalia.compat.rei.brewing.SmallCauldronDisplay;
import net.astralya.hexalia.compat.rei.brazier.RitualBrazierCategory;
import net.astralya.hexalia.compat.rei.brazier.RitualBrazierDisplay;
import net.astralya.hexalia.compat.rei.transmutation.RitualTableCategory;
import net.astralya.hexalia.compat.rei.transmutation.RitualTableDisplay;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.screen.SmallCauldronScreen;

public class HexaliaREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SmallCauldronCategory());
        registry.add(new RitualTableCategory());
        registry.add(new RitualBrazierCategory());

        registry.addWorkstations(SmallCauldronCategory.SMALL_CAULDRON,
                EntryStacks.of(ModItems.SMALL_CAULDRON));
        registry.addWorkstations(RitualTableCategory.RITUAL_TABLE,
                EntryStacks.of(ModItems.RITUAL_TABLE));
        registry.addWorkstations(RitualBrazierCategory.RITUAL_BRAZIER,
                EntryStacks.of(ModBlocks.RITUAL_BRAZIER));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(SmallCauldronRecipe.class, SmallCauldronRecipe.Type.INSTANCE,
                SmallCauldronDisplay::new);
        registry.registerRecipeFiller(RitualTableRecipe.class, RitualTableRecipe.Type.INSTANCE,
                RitualTableDisplay::new);
        registry.registerRecipeFiller(RitualBrazierRecipe.class, RitualBrazierRecipe.Type.INSTANCE,
                RitualBrazierDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(89, 25, 24, 17), SmallCauldronScreen.class,
                SmallCauldronCategory.SMALL_CAULDRON);
    }

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }
}
