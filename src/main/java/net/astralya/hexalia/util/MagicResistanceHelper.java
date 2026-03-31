package net.astralya.hexalia.util;

import net.astralya.hexalia.item.custom.armor.MagicResistanceArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MagicResistanceHelper {
    private MagicResistanceHelper() {}

    public static float getMagicResistancePct(LivingEntity entity) {
        return clamp01(MagicResistanceUtil.getTotalMagicResistance(entity));
    }

    public static boolean isWearingFullSetGroup(LivingEntity entity, ResourceLocation groupId) {
        return MagicResistanceUtil.isWearingFullSet(entity, groupId);
    }

    public static float getGroupBonusForEntity(LivingEntity entity, ResourceLocation groupId) {
        if (!MagicResistanceUtil.isWearingFullSet(entity, groupId)) return 0.0f;
        float bonus = 0.0f;
        for (EquipmentSlot slot : new EquipmentSlot[]{ EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof MagicResistanceArmor armor && groupId.equals(armor.getArmorSetId())) {
                bonus = Math.max(bonus, MagicResistanceUtil.FULL_SET_BONUS);
            }
        }
        return bonus;
    }

    private static float clamp01(float value) {
        if (value <= 0.0f) return 0.0f;
        return Math.min(value, 1.0f);
    }
}