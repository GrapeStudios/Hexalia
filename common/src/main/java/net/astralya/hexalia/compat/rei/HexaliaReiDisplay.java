package net.astralya.hexalia.compat.rei;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class HexaliaReiDisplay extends BasicDisplay {
  private final CategoryIdentifier<HexaliaReiDisplay> category;
  private final Layout layout;
  private final List<Component> recipeTooltips;

  public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs) {
    this(category, layout, inputs, outputs, List.of(), Optional.empty());
  }

  public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      Optional<ResourceLocation> location) {
    this(category, layout, inputs, outputs, List.of(), location);
  }

  public HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      List<Component> recipeTooltips) {
    this(category, layout, inputs, outputs, recipeTooltips, Optional.empty());
  }

  private HexaliaReiDisplay(
      CategoryIdentifier<HexaliaReiDisplay> category,
      Layout layout,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      List<Component> recipeTooltips,
      Optional<ResourceLocation> location) {
    super(inputs, outputs, location);
    this.category = category;
    this.layout = layout;
    this.recipeTooltips = List.copyOf(recipeTooltips);
  }

  @Override
  public CategoryIdentifier<?> getCategoryIdentifier() {
    return category;
  }

  public Layout getLayout() {
    return layout;
  }

  public List<Component> getRecipeTooltips() {
    return recipeTooltips;
  }

  public enum Layout {
    MORTAR_AND_PESTLE,
    SMALL_CAULDRON,
    NATURES_RITUAL,
    CELESTIAL_INFUSION,
    MUTATION
  }
}
