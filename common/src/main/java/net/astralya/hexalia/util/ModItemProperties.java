package net.astralya.hexalia.util;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public final class ModItemProperties {
  private ModItemProperties() {}

  public static void register() {
    registerBottledMoth();
    registerThornbow();
    registerSpiritrootTether();
  }

  private static void registerBottledMoth() {
    ItemPropertiesRegistry.register(
        ModItems.BOTTLED_MOTH.get(),
        ResourceLocation.fromNamespaceAndPath("hexalia", "variant"),
        (stack, level, entity, seed) -> {
          DataComponentType<MothData> type = ModComponents.MOTH.get();
          MothData data = stack.get(type);
          return data != null ? (float) data.variantId() : 0.0F;
        });
  }

  private static void registerThornbow() {
    ItemPropertiesRegistry.register(
        ModItems.THORNBOW.get(),
        ResourceLocation.withDefaultNamespace("pull"),
        (stack, level, entity, seed) -> {
          if (!(entity instanceof LivingEntity living) || living.getUseItem() != stack) {
            return 0.0F;
          }
          return (float) (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F;
        });

    ItemPropertiesRegistry.register(
        ModItems.THORNBOW.get(),
        ResourceLocation.withDefaultNamespace("pulling"),
        (stack, level, entity, seed) -> {
          if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) {
            return 0.0F;
          }
          return living.getUseItem() == stack ? 1.0F : 0.0F;
        });
  }

  private static void registerSpiritrootTether() {
    ItemPropertiesRegistry.register(
        ModItems.SPIRITROOT_TETHER.get(),
        ResourceLocation.fromNamespaceAndPath("hexalia", "bound"),
        (stack, level, entity, seed) -> {
          DataComponentType<SpiritrootTetherData> type = ModComponents.SPIRITROOT_TETHER.get();
          SpiritrootTetherData data = stack.get(type);
          return data != null && data.hasMob() ? 1.0F : 0.0F;
        });
  }
}
