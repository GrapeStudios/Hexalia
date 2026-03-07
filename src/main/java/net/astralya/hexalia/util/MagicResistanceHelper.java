package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MagicResistanceHelper {

    private MagicResistanceHelper() {}

    public static float getMagicResistancePct(LivingEntity entity) {
        ItemStack head  = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs  = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet  = entity.getItemBySlot(EquipmentSlot.FEET);

        float total = getPiecePct(head) + getPiecePct(chest) + getPiecePct(legs) + getPiecePct(feet);

        ResourceLocation groupId = getSharedGroupId(head, chest, legs, feet);
        if (groupId != null) {
            total += getGroupBonus(head, chest, legs, feet, groupId);
        }

        return clamp01(total);
    }

    public static boolean isWearingFullSetGroup(LivingEntity entity, ResourceLocation groupId) {
        return matchesGroup(entity.getItemBySlot(EquipmentSlot.HEAD),  groupId)
                && matchesGroup(entity.getItemBySlot(EquipmentSlot.CHEST), groupId)
                && matchesGroup(entity.getItemBySlot(EquipmentSlot.LEGS),  groupId)
                && matchesGroup(entity.getItemBySlot(EquipmentSlot.FEET),  groupId);
    }

    public static float getGroupBonusForEntity(LivingEntity entity, ResourceLocation groupId) {
        float bonus = 0.0f;
        for (EquipmentSlot slot : new EquipmentSlot[]{ EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (matchesGroup(stack, groupId)) {
                Float b = stack.get(ModComponents.FULL_SET_BONUS_PCT.get());
                if (b != null) bonus = Math.max(bonus, b);
            }
        }
        return bonus;
    }

    private static float getPiecePct(ItemStack stack) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT.get());
        return pct != null ? pct : 0.0f;
    }

    private static ResourceLocation getSharedGroupId(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        ResourceLocation g = head.get(ModComponents.ARMOR_SET_GROUP_ID.get());
        if (g == null) return null;
        if (!g.equals(chest.get(ModComponents.ARMOR_SET_GROUP_ID.get()))) return null;
        if (!g.equals(legs.get(ModComponents.ARMOR_SET_GROUP_ID.get()))) return null;
        if (!g.equals(feet.get(ModComponents.ARMOR_SET_GROUP_ID.get()))) return null;
        return g;
    }

    private static float getGroupBonus(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ResourceLocation groupId) {
        float bonus = 0.0f;
        for (ItemStack stack : new ItemStack[]{ head, chest, legs, feet }) {
            if (matchesGroup(stack, groupId)) {
                Float b = stack.get(ModComponents.FULL_SET_BONUS_PCT.get());
                if (b != null) bonus = Math.max(bonus, b);
            }
        }
        return bonus;
    }

    private static boolean matchesGroup(ItemStack stack, ResourceLocation groupId) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = stack.get(ModComponents.ARMOR_SET_GROUP_ID.get());
        return id != null && id.equals(groupId);
    }

    private static float clamp01(float value) {
        if (value <= 0.0f) return 0.0f;
        return Math.min(value, 1.0f);
    }
}