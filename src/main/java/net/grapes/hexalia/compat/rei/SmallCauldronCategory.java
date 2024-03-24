package net.grapes.hexalia.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.grapes.hexalia.HexaliaMod;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class SmallCauldronCategory implements DisplayCategory<BasicDisplay> {

    public static final Identifier TEXTURE =
            new Identifier(HexaliaMod.MOD_ID, "textures/gui/small_cauldron_gui.png");
    public static final CategoryIdentifier<SmallCauldronDisplay> SMALL_CAULDRON =
            CategoryIdentifier.of(HexaliaMod.MOD_ID, "small_cauldron");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return SMALL_CAULDRON;
    }

    @Override
    public Text getTitle() {
        return Text.literal("Small Cauldron");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.SMALL_CAULDRON.getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle startPoint = HexaliaREIClientPlugin.centeredIntoRecipeBase(origin, 116, 56);
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint, 29, 16));

        List<EntryIngredient> ingredientEntries = display.getInputEntries();
        if (ingredientEntries != null) {
            for (int i = 0; i < ingredientEntries.size(); i++) {
                Point slotLoc = new Point(startPoint.x + 1 + i % 3 * 18, startPoint.y + 1 + (i / 3) * 18);
                widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput().disableBackground());
            }
        }

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 12))
                .entries(display.getOutputEntries().get(0)).markOutput().disableBackground());
        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 95, startPoint.y + 40, 17, 15),
                Text.translatable("tooltip.hexalia.needs_heat")));
        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 35, startPoint.y + 35, 17, 15),
                Text.translatable("tooltip.hexalia.needs_rustic_bottle")));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
