package net.astralya.hexalia.compat.rei.mortar_and_pestle;

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
import net.astralya.hexalia.compat.rei.HexaliaREIClientPlugin;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class MortarAndPestleCategory implements DisplayCategory<MortarAndPestleDisplay> {

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/mortar_gui.png");

    public static final CategoryIdentifier<MortarAndPestleDisplay> MORTAR_AND_PESTLE =
            CategoryIdentifier.of(ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "mortar_and_pestle"));

    @Override
    public CategoryIdentifier<? extends MortarAndPestleDisplay> getCategoryIdentifier() {
        return MORTAR_AND_PESTLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.hexalia.mortar_and_pestle");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.MORTAR_AND_PESTLE.get());
    }

    @Override
    public List<Widget> setupDisplay(MortarAndPestleDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int guiWidth = 118;
        int guiHeight = 80;

        Rectangle startPoint = HexaliaREIClientPlugin.centeredIntoRecipeBase(origin, guiWidth, guiHeight);
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x, startPoint.y, 0, 0, guiWidth, guiHeight));

        List<EntryIngredient> inputs = display.getInputEntries();
        if (!inputs.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 28, startPoint.y + 31))
                    .entries(inputs.getFirst())
                    .markInput()
                    .disableBackground());
        }
        if (inputs.size() >= 2) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 31))
                    .entries(inputs.get(1))
                    .markInput()
                    .disableBackground());
        }
        if (inputs.size() >= 3) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 52, startPoint.y + 31))
                    .entries(inputs.get(2))
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

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }
}