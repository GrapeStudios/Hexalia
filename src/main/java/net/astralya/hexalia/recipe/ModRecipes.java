package net.astralya.hexalia.recipe;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {

    private static final String SMALL_CAULDRON_ID = "small_cauldron";
    private static final String TRANSMUTATION_ID = "transmutation";
    private static final String RITUAL_BRAZIER_ID = "ritual_brazier";

    public static void registerRecipes() {
        registerRecipeType(SMALL_CAULDRON_ID,
                SmallCauldronRecipe.Serializer.INSTANCE,
                SmallCauldronRecipe.Type.INSTANCE);

        registerRecipeType(TRANSMUTATION_ID,
                TransmutationRecipe.Serializer.INSTANCE,
                TransmutationRecipe.Type.INSTANCE);

        registerRecipeType(RITUAL_BRAZIER_ID,
                RitualBrazierRecipe.Serializer.INSTANCE,
                RitualBrazierRecipe.Type.INSTANCE);
    }

    private static void registerRecipeType(String id,
                                           RecipeSerializer<?> serializer,
                                           RecipeType<?> type) {
        Identifier typeId = new Identifier(HexaliaMod.MOD_ID, id);
        Registry.register(Registries.RECIPE_SERIALIZER, typeId, serializer);
        Registry.register(Registries.RECIPE_TYPE, typeId, type);
    }

    public static Identifier getSmallCauldronId() {
        return new Identifier(HexaliaMod.MOD_ID, SMALL_CAULDRON_ID);
    }

    public static Identifier getTransmutationId() {
        return new Identifier(HexaliaMod.MOD_ID, TRANSMUTATION_ID);
    }

    public static Identifier getRitualBrazierId() {
        return new Identifier(HexaliaMod.MOD_ID, RITUAL_BRAZIER_ID);
    }
}