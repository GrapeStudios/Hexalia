package net.astralya.hexalia.util;

import net.astralya.hexalia.HexaliaMod;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ModItemProperties {

    public static void addCustomItemProperties() {
        registerBottledMoth();
        registerThornbow();
    }

    private static void registerBottledMoth() {
        ItemProperties.register(
                ModItems.BOTTLED_MOTH.get(),
                ResourceLocation.fromNamespaceAndPath(HexaliaMod.MODID, "variant"),
                (stack, level, entity, seed) -> {
                    MothData data = stack.get(ModComponents.MOTH.get());
                    return data != null ? (float) data.variantId() : 0.0F;
                }
        );
    }

    private static void registerThornbow() {
        ItemProperties.register(
                ModItems.THORNBOW.get(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return 0.0F;
                    }
                    if (living.getUseItem() != stack) {
                        return 0.0F;
                    }
                    return (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F;
                }
        );

        ItemProperties.register(
                ModItems.THORNBOW.get(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                        entity instanceof LivingEntity living && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F
        );
    }
}
