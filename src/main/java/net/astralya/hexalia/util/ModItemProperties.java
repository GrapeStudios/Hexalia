package net.astralya.hexalia.util;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
                new ResourceLocation("hexalia", "variant"),
                (stack, level, entity, seed) -> {
                    CompoundTag tag = stack.getTag();
                    if (tag == null || !tag.contains("variantId")) return 0.0F;
                    return (float) tag.getInt("variantId");
                }
        );
    }

    private static void registerThornbow() {
        ItemProperties.register(
                (Item) ModItems.THORNBOW.get(),
                new ResourceLocation("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    if (entity.getUseItem() != stack) return 0.0F;
                    return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                }
        );
        ItemProperties.register(
                (Item) ModItems.THORNBOW.get(),
                new ResourceLocation("pulling"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    if (!entity.isUsingItem()) return 0.0F;
                    if (entity.getUseItem() != stack) return 0.0F;
                    return 1.0F;
                }
        );
    }

    private static void registerSpiritrootTether() {
        ItemProperties.register(
                (Item) ModItems.SPIRITROOT_TETHER.get(),
                new ResourceLocation("hexalia", "bound"),
                (stack, level, entity, seed) -> {
                    CompoundTag tag = stack.getTag();
                    if (tag == null) return 0.0F;
                    return tag.contains("boundMobUUID") ? 1.0F : 0.0F;
                }
        );
    }
}