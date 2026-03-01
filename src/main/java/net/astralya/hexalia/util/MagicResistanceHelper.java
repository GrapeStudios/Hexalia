package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MagicResistanceHelper {
    private MagicResistanceHelper() {
    }

    public static float getMagicResistancePct(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);

        float total = getPiecePct(head) + getPiecePct(chest) + getPiecePct(legs) + getPiecePct(feet);

        ResourceLocation setId = getFullSetId(head, chest, legs, feet);
        if (setId != null) {
            total += getFullSetBonus(head, chest, legs, feet, setId);
        }

        return clamp01(total);
    }

    private static float getPiecePct(ItemStack stack) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT.get());
        return pct != null ? pct : 0.0f;
    }

    private static ResourceLocation getFullSetId(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) {
            return null;
        }

        ResourceLocation a = head.get(ModComponents.ARMOR_SET_ID.get());
        if (a == null) {
            return null;
        }

        ResourceLocation b = chest.get(ModComponents.ARMOR_SET_ID.get());
        ResourceLocation c = legs.get(ModComponents.ARMOR_SET_ID.get());
        ResourceLocation d = feet.get(ModComponents.ARMOR_SET_ID.get());

        if (a.equals(b) && a.equals(c) && a.equals(d)) {
            return a;
        }

        return null;
    }

    private static float getFullSetBonus(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ResourceLocation setId) {
        float bonus = 0.0f;

        bonus = Math.max(bonus, bonusIfMatches(head, setId));
        bonus = Math.max(bonus, bonusIfMatches(chest, setId));
        bonus = Math.max(bonus, bonusIfMatches(legs, setId));
        bonus = Math.max(bonus, bonusIfMatches(feet, setId));

        return bonus;
    }

    private static float bonusIfMatches(ItemStack stack, ResourceLocation setId) {
        ResourceLocation id = stack.get(ModComponents.ARMOR_SET_ID.get());
        if (id == null || !id.equals(setId)) {
            return 0.0f;
        }

        Float bonus = stack.get(ModComponents.FULL_SET_BONUS_PCT.get());
        return bonus != null ? bonus : 0.0f;
    }

    private static float clamp01(float value) {
        if (value <= 0.0f) {
            return 0.0f;
        }
        return Math.min(value, 1.0f);
    }
}