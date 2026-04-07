package net.astralya.hexalia.compat.rei.ritual_table;

import java.util.LinkedList;
import java.util.List;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.compat.rei.HexaliaREIClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RitualTableCategory implements DisplayCategory<RitualTableDisplay> {

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/ritual_table_gui.png");

    public static final CategoryIdentifier<RitualTableDisplay> RITUAL_TABLE =
            CategoryIdentifier.of(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "ritual_table"));

    @Override
    public CategoryIdentifier<? extends RitualTableDisplay> getCategoryIdentifier() {
        return RITUAL_TABLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hexalia.ritual_table");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.RITUAL_TABLE.get());
    }

    @Override
    public List<Widget> setupDisplay(RitualTableDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int guiWidth = 118;
        int guiHeight = 72;

        Rectangle startPoint = HexaliaREIClientPlugin.centeredIntoRecipeBase(origin, guiWidth, guiHeight);
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x, startPoint.y, 0, 0, guiWidth, guiHeight));

        List<EntryIngredient> ingredientEntries = display.getInputEntries();

        if (!ingredientEntries.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 28, startPoint.y + 31))
                    .entries(ingredientEntries.getFirst())
                    .markInput()
                    .disableBackground());
        }

        if (ingredientEntries.size() >= 2) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 28, startPoint.y + 7))
                    .entries(ingredientEntries.get(1))
                    .markInput()
                    .disableBackground());
        }

        if (ingredientEntries.size() >= 3) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 28, startPoint.y + 55))
                    .entries(ingredientEntries.get(2))
                    .markInput()
                    .disableBackground());
        }

        if (ingredientEntries.size() >= 4) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 52, startPoint.y + 31))
                    .entries(ingredientEntries.get(3))
                    .markInput()
                    .disableBackground());
        }

        if (ingredientEntries.size() >= 5) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 31))
                    .entries(ingredientEntries.get(4))
                    .markInput()
                    .disableBackground());
        }

        List<EntryIngredient> outputs = display.getOutputEntries();
        if (!outputs.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 89, startPoint.y + 31))
                    .entries(outputs.getFirst())
                    .markOutput()
                    .disableBackground());
        }

        widgets.add(Widgets.createTexturedWidget(
                TEXTURE,
                startPoint.x + 4, startPoint.y + 55,
                0, 0, 16, 16
        ));

        widgets.add(Widgets.createTooltip(
                new Rectangle(startPoint.x + 4, startPoint.y + 55, 16, 16),
                Component.translatable("tooltip.hexalia.hex_focus_gui")
        ));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }
}