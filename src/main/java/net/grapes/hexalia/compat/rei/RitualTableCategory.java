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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class RitualTableCategory implements DisplayCategory<BasicDisplay> {

    public static final Identifier TEXTURE =
            new Identifier(HexaliaMod.MOD_ID, "textures/gui/ritual_table_gui.png");
    public static final CategoryIdentifier<RitualTableDisplay> RITUAL_TABLE =
            CategoryIdentifier.of(HexaliaMod.MOD_ID, "ritual_table");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return RITUAL_TABLE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.hexalia.ritual_table");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.RITUAL_TABLE.getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        List<Widget> widgets = new LinkedList<>();

        // Adjust the overall size of the recipe base
        widgets.add(Widgets.createRecipeBase(bounds));

        // The actual GUI dimensions (80 width, 48 height)
        int guiWidth = 118;
        int guiHeight = 80;

        int fixedY = 39;
        int fixedX = 28;

        // Center the startPoint correctly within the bounds
        Rectangle startPoint = HexaliaREIClientPlugin.centeredIntoRecipeBase(origin, guiWidth, guiHeight);

        // Draw the GUI texture, using the start point correctly
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x, startPoint.y, 0, -8, guiWidth, guiHeight));

        // Retrieve the input entries (input + salt items)
        List<EntryIngredient> ingredientEntries = display.getInputEntries();

        if (ingredientEntries != null && !ingredientEntries.isEmpty()) {
            // Center slot (Transmutable Item - input)
            widgets.add(Widgets.createSlot(new Point(startPoint.x + fixedX, startPoint.y + fixedY))
                    .entries(ingredientEntries.get(0)) // The central input item
                    .markInput().disableBackground());

            // Assuming salt items are in the remaining slots (indices 1 to 4)
            if (ingredientEntries.size() >= 5) {
                // Adjusted coordinates to match your GUI image:
                // Top ingredient slot
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + fixedY))
                        .entries(ingredientEntries.get(1))
                        .markInput().disableBackground());

                // Bottom ingredient slot
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 52, startPoint.y + fixedY))
                        .entries(ingredientEntries.get(2))
                        .markInput().disableBackground());

                // Left ingredient slot
                widgets.add(Widgets.createSlot(new Point(startPoint.x + fixedX, startPoint.y + 15))
                        .entries(ingredientEntries.get(3))
                        .markInput().disableBackground());

                // Right ingredient slot
                widgets.add(Widgets.createSlot(new Point(startPoint.x + fixedX, startPoint.y + 63))
                        .entries(ingredientEntries.get(4))
                        .markInput().disableBackground());

                widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 4, startPoint.y + 63, 16, 16),
                        Text.translatable("tooltip.hexalia.ritual_table_gui")));
            }
        }

        // Output slot next to the arrow (adjusted coordinates)
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 89, startPoint.y + fixedY))
                .entries(display.getOutputEntries().get(0)) // The output item
                .markOutput().disableBackground());

        return widgets;
    }


    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
