package net.astralya.hexalia.recipe;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {

    public static final RecipeSerializer<RitualBrazierRecipe> RITUAL_BRAZIER_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(HexaliaMod.MODID, "ritual_brazier"), new RitualBrazierRecipe.Serializer());
    public static final RecipeType<RitualBrazierRecipe> RITUAL_BRAZIER_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(HexaliaMod.MODID, "ritual_brazier"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "ritual_brazier";
                }
            });

    public static final RecipeSerializer<SmallCauldronRecipe> SMALL_CAULDRON_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(HexaliaMod.MODID, "small_cauldron"), new SmallCauldronRecipe.Serializer());
    public static final RecipeType<SmallCauldronRecipe> SMALL_CAULDRON_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(HexaliaMod.MODID, "small_cauldron"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "small_cauldron";
                }
            });

    public static final RecipeSerializer<RitualTableRecipe> RITUAL_TABLE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(HexaliaMod.MODID, "ritual_table"), new RitualTableRecipe.Serializer());
    public static final RecipeType<RitualTableRecipe> RITUAL_TABLE_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(HexaliaMod.MODID, "ritual_table"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "ritual_table";
                }
            });

    public static final RecipeSerializer<MutationRecipe> MUTATION_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(HexaliaMod.MODID, "mutation"), new MutationRecipe.Serializer());
    public static final RecipeType<MutationRecipe> MUTATION_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(HexaliaMod.MODID, "mutation"), new RecipeType<>() {
                @Override
                public String toString() {
                    return "mutation";
                }
            });

    public static void registerRecipes() {
        HexaliaMod.LOGGER.info("Registering Custom Recipes for " + HexaliaMod.MODID);
    }
}
