package net.astralya.hexalia.compat.patchouli;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.RitualBrazierRecipe;
import net.astralya.hexalia.recipe.RitualTableRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class RecipePageComponent implements ICustomComponent {

    private static final int PAGE_WIDTH = 118;
    private static final int ITEM_OFFSET_X = 1;
    private static final int ITEM_OFFSET_Y = 1;
    private static final ResourceLocation MUTATION_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/mutation_gui.png");
    private static final ResourceLocation MORTAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/mortar_gui.png");
    private static final ResourceLocation RITUAL_BRAZIER_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/ritual_brazier_gui.png");
    private static final ResourceLocation RITUAL_TABLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/ritual_table_gui.png");
    private static final ResourceLocation SMALL_CAULDRON_TEXTURE = ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "textures/gui/category/small_cauldron_gui.png");

    public String recipe_id = "";
    public String layout = "";

    private transient int x;
    private transient int y;
    private transient RecipeView recipe;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (recipe == null) {
            graphics.drawString(Minecraft.getInstance().font, "Missing recipe: " + recipe_id, x, y + 28, 0xFF404040, false);
            return;
        }

        graphics.drawString(
                Minecraft.getInstance().font,
                recipe.output.getHoverName(),
                x + (PAGE_WIDTH / 2) - (Minecraft.getInstance().font.width(recipe.output.getHoverName()) / 2),
                y,
                0xFF404040,
                false
        );

        renderRecipe(graphics, context, recipe, mouseX, mouseY);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, net.minecraft.core.HolderLookup.Provider registries) {
        recipe_id = lookup.apply(IVariable.wrap(recipe_id, registries)).asString();
        layout = lookup.apply(IVariable.wrap(layout, registries)).asString();
        recipe = loadRecipe(recipe_id, layout).orElse(null);
    }

    private void renderRecipe(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        switch (layout) {
            case "small_cauldron" -> renderSmallCauldron(graphics, context, currentRecipe, mouseX, mouseY);
            case "ritual_table" -> renderRitualTable(graphics, context, currentRecipe, mouseX, mouseY);
            case "mortar_and_pestle" -> renderMortar(graphics, context, currentRecipe, mouseX, mouseY);
            case "mutation" -> renderMutation(graphics, context, currentRecipe, mouseX, mouseY);
            case "ritual_brazier" -> renderRitualBrazier(graphics, context, currentRecipe, mouseX, mouseY);
            default -> graphics.drawString(Minecraft.getInstance().font, "Unsupported layout: " + layout, x, y + 28, 0xFF404040, false);
        }
    }

    private void renderMutation(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        int left = centeredX(118);
        int top = y + 18;
        graphics.blit(MUTATION_TEXTURE, left, top, 0, 0, 118, 80, 256, 256);
        context.renderIngredient(graphics, left + 47 + ITEM_OFFSET_X, top + 31 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.ingredients.getFirst());
        context.renderItemStack(graphics, left + 88 + ITEM_OFFSET_X, top + 30 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.output);
    }

    private void renderMortar(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        int left = centeredX(118);
        int top = y + 18;
        graphics.blit(MORTAR_TEXTURE, left, top, 0, 0, 118, 80, 256, 256);
        int[][] slots = {
                {left + 3, top + 30},
                {left + 27, top + 30},
                {left + 51, top + 30}
        };

        for (int i = 0; i < slots.length; i++) {
            if (i < currentRecipe.ingredients.size()) {
                context.renderIngredient(graphics, slots[i][0] + ITEM_OFFSET_X, slots[i][1] + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.ingredients.get(i));
            }
        }
        context.renderItemStack(graphics, left + 88 + ITEM_OFFSET_X, top + 30 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.output);
    }

    private void renderSmallCauldron(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        int left = centeredX(89);
        int top = y + 30;
        graphics.blit(SMALL_CAULDRON_TEXTURE, left, top, 14, 19, 89, 42, 256, 256);
        int[][] slots = {
                {left, top},
                {left + 24, top},
                {left, top + 24},
                {left + 24, top + 24}
        };

        for (int i = 0; i < slots.length; i++) {
            if (i < currentRecipe.ingredients.size()) {
                context.renderIngredient(graphics, slots[i][0] + ITEM_OFFSET_X, slots[i][1] + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.ingredients.get(i));
            }
        }
        context.renderItemStack(graphics, left + 69 + ITEM_OFFSET_X, top + 11 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.output);
    }

    private void renderRitualBrazier(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        int left = centeredX(118);
        int top = y + 18;
        graphics.blit(RITUAL_BRAZIER_TEXTURE, left, top, 0, 0, 118, 80, 256, 256);
        context.renderIngredient(graphics, left + 27 + ITEM_OFFSET_X, top + 30 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.ingredients.getFirst());
        context.renderItemStack(graphics, left + 88 + ITEM_OFFSET_X, top + 30 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.output);
    }

    private void renderRitualTable(GuiGraphics graphics, IComponentRenderContext context, RecipeView currentRecipe, int mouseX, int mouseY) {
        int left = centeredX(118);
        int top = y + 18;
        graphics.blit(RITUAL_TABLE_TEXTURE, left, top, 0, 0, 118, 80, 256, 256);
        int[][] slots = {
                {left + 27, top + 30},
                {left + 3, top + 30},
                {left + 51, top + 30},
                {left + 27, top + 6},
                {left + 27, top + 54}
        };

        for (int i = 0; i < slots.length; i++) {
            if (i < currentRecipe.ingredients.size()) {
                context.renderIngredient(graphics, slots[i][0] + ITEM_OFFSET_X, slots[i][1] + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.ingredients.get(i));
            }
        }
        context.renderItemStack(graphics, left + 88 + ITEM_OFFSET_X, top + 30 + ITEM_OFFSET_Y, mouseX, mouseY, currentRecipe.output);
    }

    private int centeredX(int width) {
        return x + ((PAGE_WIDTH - width) / 2);
    }

    private static Optional<RecipeView> loadRecipe(String recipeId, String recipeLayout) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return Optional.empty();
        }

        ResourceLocation id = ResourceLocation.tryParse(recipeId);
        if (id == null) {
            return Optional.empty();
        }

        RecipeManager manager = client.level.getRecipeManager();

        return switch (recipeLayout) {
            case "small_cauldron" -> manager.byKey(id)
                    .filter(holder -> holder.value() instanceof SmallCauldronRecipe)
                    .map(holder -> {
                        SmallCauldronRecipe recipe = (SmallCauldronRecipe) holder.value();
                        return new RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
                    });
            case "ritual_brazier" -> manager.byKey(id)
                    .filter(holder -> holder.value() instanceof RitualBrazierRecipe)
                    .map(holder -> {
                        RitualBrazierRecipe recipe = (RitualBrazierRecipe) holder.value();
                        return new RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
                    });
            case "mutation" -> manager.byKey(id)
                    .filter(holder -> holder.value() instanceof MutationRecipe)
                    .map(holder -> {
                        MutationRecipe recipe = (MutationRecipe) holder.value();
                        return new RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
                    });
            case "mortar_and_pestle" -> manager.byKey(id)
                    .filter(holder -> holder.value() instanceof MortarAndPestleRecipe)
                    .map(holder -> {
                        MortarAndPestleRecipe recipe = (MortarAndPestleRecipe) holder.value();
                        return new RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
                    });
            case "ritual_table" -> manager.byKey(id)
                    .filter(holder -> holder.value() instanceof RitualTableRecipe)
                    .map(holder -> {
                        RitualTableRecipe recipe = (RitualTableRecipe) holder.value();
                        return new RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
                    });
            default -> Optional.empty();
        };
    }

    private record RecipeView(List<Ingredient> ingredients, ItemStack output) {
    }
}
