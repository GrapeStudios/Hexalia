package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class MagicResistanceHelper {

    private MagicResistanceHelper() {
    }

    public static float getMagicResistancePct(LivingEntity entity) {
        ItemStack head  = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs  = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet  = entity.getEquippedStack(EquipmentSlot.FEET);
        float total = getPiecePct(head) + getPiecePct(chest) + getPiecePct(legs) + getPiecePct(feet);
        Identifier groupId = getSharedGroupId(head, chest, legs, feet);
        if (groupId != null) {
            total += getGroupBonus(head, chest, legs, feet, groupId);
        }
        return clamp01(total);
    }

    public static boolean isWearingFullSetGroup(LivingEntity entity, Identifier groupId) {
        return matchesGroup(entity.getEquippedStack(EquipmentSlot.HEAD),  groupId)
                && matchesGroup(entity.getEquippedStack(EquipmentSlot.CHEST), groupId)
                && matchesGroup(entity.getEquippedStack(EquipmentSlot.LEGS),  groupId)
                && matchesGroup(entity.getEquippedStack(EquipmentSlot.FEET),  groupId);
    }

    public static float getGroupBonusForEntity(LivingEntity entity, Identifier groupId) {
        float bonus = 0.0f;
        for (EquipmentSlot slot : new EquipmentSlot[]{ EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (matchesGroup(stack, groupId)) {
                Float b = stack.get(ModComponents.FULL_SET_BONUS_PCT);
                if (b != null) bonus = Math.max(bonus, b);
            }
        }
        return bonus;
    }

    private static float getPiecePct(ItemStack stack) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT);
        return pct != null ? pct : 0.0f;
    }

    private static Identifier getSharedGroupId(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        Identifier g = head.get(ModComponents.ARMOR_SET_GROUP_ID);
        if (g == null) return null;
        if (!g.equals(chest.get(ModComponents.ARMOR_SET_GROUP_ID))) return null;
        if (!g.equals(legs.get(ModComponents.ARMOR_SET_GROUP_ID))) return null;
        if (!g.equals(feet.get(ModComponents.ARMOR_SET_GROUP_ID))) return null;
        return g;
    }

    private static float getGroupBonus(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, Identifier groupId) {
        float bonus = 0.0f;
        for (ItemStack stack : new ItemStack[]{ head, chest, legs, feet }) {
            if (matchesGroup(stack, groupId)) {
                Float b = stack.get(ModComponents.FULL_SET_BONUS_PCT);
                if (b != null) bonus = Math.max(bonus, b);
            }
        }
        return bonus;
    }

    private static boolean matchesGroup(ItemStack stack, Identifier groupId) {
        if (stack.isEmpty()) return false;
        Identifier id = stack.get(ModComponents.ARMOR_SET_GROUP_ID);
        return id != null && id.equals(groupId);
    }

    private static float clamp01(float value) {
        if (value <= 0.0f) return 0.0f;
        return Math.min(value, 1.0f);
    }
}