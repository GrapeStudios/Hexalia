package net.astralya.hexalia.compat.rei.ritual_brazier;

import java.util.ArrayList;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RitualBrazierCategory implements DisplayCategory<RitualBrazierDisplay> {

    public static final Identifier TEXTURE = new Identifier(HexaliaMod.MODID, "textures/gui/ritual_brazier_gui.png");
    public static final CategoryIdentifier<RitualBrazierDisplay> RITUAL_BRAZIER = CategoryIdentifier.of(HexaliaMod.MODID, "ritual_brazier");

    @Override
    public CategoryIdentifier<? extends RitualBrazierDisplay> getCategoryIdentifier() {
        return RITUAL_BRAZIER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.hexalia.ritual_brazier");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.RITUAL_BRAZIER.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(RitualBrazierDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        int guiWidth = 118;
        int guiHeight = 80;
        Rectangle startPoint = HexaliaREIClientPlugin.centeredIntoRecipeBase(origin, guiWidth, guiHeight);
        widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x, startPoint.y, 0, 0, guiWidth, guiHeight));

        List<EntryIngredient> inputs = display.getInputEntries();
        if (!inputs.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 28, startPoint.y + 31))
                    .entries(inputs.get(0))
                    .markInput()
                    .disableBackground());
        }

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 89, startPoint.y + 31))
                .entries(display.getOutputEntries().get(0))
                .markOutput()
                .disableBackground());

        widgets.add(Widgets.createTexturedWidget(TEXTURE,
                startPoint.x + 51, startPoint.y + 31,
                118, 0, 16, 16));

        widgets.add(Widgets.createTooltip(new Rectangle(startPoint.x + 3, startPoint.y + 53, 16, 16),
                Text.translatable("tooltip.hexalia.hex_focus_gui")));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }
}