package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class ModItemProperties {

    public static void addCustomItemProperties() {
        registerBottledMoth();
        registerThornbow();
        registerSpiritrootTether();
    }

    private static void registerBottledMoth() {
        ItemProperties.register(
                (Item) ModItems.BOTTLED_MOTH.get(),
                ResourceLocation.fromNamespaceAndPath("hexalia", "variant"),
                (stack, level, entity, seed) -> {
                    DataComponentType<MothData> type = ModComponents.MOTH.get();
                    MothData data = stack.get(type);
                    return data != null ? (float) data.variantId() : 0.0F;
                }
        );
    }

    private static void registerThornbow() {
        ItemProperties.register(
                (Item) ModItems.THORNBOW.get(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return 0.0F;
                    }
                    if (living.getUseItem() != stack) {
                        return 0.0F;
                    }
                    return (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F;
                }
        );

        ItemProperties.register(
                (Item) ModItems.THORNBOW.get(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return 0.0F;
                    }
                    if (!living.isUsingItem()) {
                        return 0.0F;
                    }
                    if (living.getUseItem() != stack) {
                        return 0.0F;
                    }
                    return 1.0F;
                }
        );
    }

    private static void registerSpiritrootTether() {
        ItemProperties.register(
                (Item) ModItems.SPIRITROOT_TETHER.get(),
                ResourceLocation.fromNamespaceAndPath("hexalia", "bound"),
                (stack, level, entity, seed) -> {
                    DataComponentType<SpiritrootTetherData> type = ModComponents.SPIRITROOT_TETHER.get();
                    SpiritrootTetherData data = stack.get(type);
                    return data != null && data.hasMob() ? 1.0F : 0.0F;
                }
        );
    }
}
