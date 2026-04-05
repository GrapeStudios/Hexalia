package net.astralya.hexalia.util;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
                new Identifier("hexalia", "variant"),
                (stack, world, entity, seed) -> getMothVariant(stack)
        );
    }

    private static void registerThornbow() {
        ModelPredicateProviderRegistry.register(
                ModItems.THORNBOW,
                new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    if (entity.getActiveItem() != stack) {
                        return 0.0F;
                    }
                    return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
        );

        ModelPredicateProviderRegistry.register(
                ModItems.THORNBOW,
                new Identifier("pulling"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    if (!entity.isUsingItem()) {
                        return 0.0F;
                    }
                    if (entity.getActiveItem() != stack) {
                        return 0.0F;
                    }
                    return 1.0F;
                }
        );
    }

    private static void registerSpiritrootTether() {
        ModelPredicateProviderRegistry.register(
                ModItems.SPIRITROOT_TETHER,
                new Identifier("hexalia", "bound"),
                (stack, world, entity, seed) -> hasBoundMob(stack) ? 1.0F : 0.0F
        );
    }

    private static float getMothVariant(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("MothData", NbtCompound.COMPOUND_TYPE)) {
            return 0.0F;
        }
        NbtCompound mothData = nbt.getCompound("MothData");
        return mothData.contains("VariantId", NbtCompound.INT_TYPE) ? mothData.getInt("VariantId") : 0.0F;
    }

    private static boolean hasBoundMob(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("SpiritrootTetherData", NbtCompound.COMPOUND_TYPE)) {
            return false;
        }
        NbtCompound tetherData = nbt.getCompound("SpiritrootTetherData");
        return tetherData.getBoolean("HasMob");
    }
}