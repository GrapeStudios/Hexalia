package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.component.item.SpiritrootTetherData;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class ModItemProperties {

    public static void addCustomItemProperties() {
        registerBottledMoth();
        registerThornbow();
        registerSpiritrootTether();
    }

    private static void registerBottledMoth() {
        ModelPredicateProviderRegistry.register(
                ModItems.BOTTLED_MOTH,
                Identifier.of("hexalia", "variant"),
                (stack, world, entity, seed) -> {
                    ComponentType<MothData> type = ModComponents.MOTH;
                    MothData data = stack.get(type);
                    return data != null ? (float) data.variantId() : 0.0F;
                }
        );
    }

    private static void registerThornbow() {
        ModelPredicateProviderRegistry.register(
                ModItems.THORNBOW,
                Identifier.ofVanilla("pull"),
                (stack, world, entity, seed) -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return 0.0F;
                    }
                    if (living.getActiveItem() != stack) {
                        return 0.0F;
                    }
                    return (float) (stack.getMaxUseTime(living) - living.getItemUseTimeLeft()) / 20.0F;
                }
        );

        ModelPredicateProviderRegistry.register(
                ModItems.THORNBOW,
                Identifier.ofVanilla("pulling"),
                (stack, world, entity, seed) -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return 0.0F;
                    }
                    if (!living.isUsingItem()) {
                        return 0.0F;
                    }
                    if (living.getActiveItem() != stack) {
                        return 0.0F;
                    }
                    return 1.0F;
                }
        );
    }

    private static void registerSpiritrootTether() {
        ModelPredicateProviderRegistry.register(
                ModItems.SPIRITROOT_TETHER,
                Identifier.of("hexalia", "bound"),
                (stack, world, entity, seed) -> {
                    ComponentType<SpiritrootTetherData> type = ModComponents.SPIRITROOT_TETHER;
                    SpiritrootTetherData data = stack.get(type);
                    return data != null && data.hasMob() ? 1.0F : 0.0F;
                }
        );
    }
}