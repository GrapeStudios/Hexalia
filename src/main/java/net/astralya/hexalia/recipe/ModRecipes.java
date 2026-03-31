package net.astralya.hexalia.recipe;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HexaliaMod.MODID);

    public static final RegistryObject<RecipeSerializer<SmallCauldronRecipe>> SMALL_CAULDRON_SERIALIZER =
            SERIALIZERS.register("small_cauldron", () -> SmallCauldronRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<RitualTableRecipe>> RITUAL_TABLE_SERIALIZER =
            SERIALIZERS.register("ritual_table", () -> RitualTableRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<RitualBrazierRecipe>> RITUAL_BRAZIER_SERIALIZER =
            SERIALIZERS.register("ritual_brazier", () -> RitualBrazierRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<MutationRecipe>> MUTATION_SERIALIZER =
            SERIALIZERS.register("mutation", () -> MutationRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<MortarAndPestleRecipe>> MORTAR_AND_PESTLE_SERIALIZER =
            SERIALIZERS.register("mortar_and_pestle", () -> MortarAndPestleRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
