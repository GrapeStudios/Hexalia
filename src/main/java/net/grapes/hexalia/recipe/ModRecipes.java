package net.grapes.hexalia.recipe;

import net.grapes.hexalia.HexaliaMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HexaliaMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<SmallCauldronRecipe>> SMALL_CAULDRON_SERIALIZER =
            SERIALIZERS.register("small_cauldron", () -> SmallCauldronRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<TransmutationRecipe>> TRANSMUTATION_SERIALIZER =
            SERIALIZERS.register("transmutation", () -> TransmutationRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
