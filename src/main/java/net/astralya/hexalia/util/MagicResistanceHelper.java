package net.astralya.hexalia.util;

import net.astralya.hexalia.item.custom.armor.MagicResistanceArmor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class MagicResistanceHelper {

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private MagicResistanceHelper() {
    }

    public static float getMagicResistancePct(LivingEntity entity) {
        return clamp01(MagicResistanceUtil.getTotalMagicResistance(entity));
    }

    public static boolean isWearingFullSetGroup(LivingEntity entity, Identifier groupId) {
        return MagicResistanceUtil.isWearingFullSetGroup(entity, groupId);
    }

    public static float getGroupBonusForEntity(LivingEntity entity, Identifier groupId) {
        if (!MagicResistanceUtil.isWearingFullSetGroup(entity, groupId)) {
            return 0.0F;
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!(stack.getItem() instanceof MagicResistanceArmor armor)) {
                return 0.0F;
            }
            if (!groupId.equals(armor.getArmorSetGroupId())) {
                return 0.0F;
            }
        }

        return MagicResistanceUtil.FULL_SET_BONUS;
    }

    private static float clamp01(float value) {
        if (value <= 0.0F) {
            return 0.0F;
        }
        return Math.min(value, 1.0F);
    }
}