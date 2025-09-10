package net.astralya.hexalia.recipe;

import net.astralya.hexalia.HexaliaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, HexaliaMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, HexaliaMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RitualBrazierRecipe>> RITUAL_BRAZIER_SERIALIZER =
            SERIALIZERS.register("ritual_brazier", RitualBrazierRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<RitualBrazierRecipe>> RITUAL_BRAZIER_TYPE =
            TYPES.register("ritual_brazier", () -> new RecipeType<RitualBrazierRecipe>() {
                @Override
                public String toString() {
                    return "ritual_brazier";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmallCauldronRecipe>> SMALL_CAULDRON_SERIALIZER =
            SERIALIZERS.register("small_cauldron", SmallCauldronRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SmallCauldronRecipe>> SMALL_CAULDRON_TYPE =
            TYPES.register("small_cauldron", () -> new RecipeType<SmallCauldronRecipe>() {
                @Override
                public String toString() {
                    return "small_cauldron";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RitualTableRecipe>> RITUAL_TABLE_SERIALIZER =
            SERIALIZERS.register("ritual_table", RitualTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<RitualTableRecipe>> RITUAL_TABLE_TYPE =
            TYPES.register("ritual_table", () -> new RecipeType<RitualTableRecipe>() {
                @Override
                public String toString() {
                    return "ritual_table";
                }
            });
    
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MutationRecipe>> MUTATION_SERIALIZER =
            SERIALIZERS.register("mutation", MutationRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MutationRecipe>> MUTATION_TYPE =
            TYPES.register("mutation", () -> new RecipeType<MutationRecipe>() {
                @Override
                public String toString() {
                    return "mutation";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
