package net.astralya.hexalia.util;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.armor.MagicResistanceArmor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class MagicResistanceUtil {

    public static final float FULL_SET_BONUS = 0.10F;

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private MagicResistanceUtil() {
    }

    public static float getTotalMagicResistance(LivingEntity entity) {
        float total = 0.0F;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack.getItem() instanceof MagicResistanceArmor armor) {
                total += armor.getMagicResistanceBonus();
            }
        }

        if (isWearingFullSetGroup(entity, ModItems.WOVEN_GROUP_ID)) {
            total += FULL_SET_BONUS;
        }

        return total;
    }

    public static boolean isWearingFullSetGroup(LivingEntity entity, Identifier groupId) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!(stack.getItem() instanceof MagicResistanceArmor armor)) {
                return false;
            }
            if (!groupId.equals(armor.getArmorSetGroupId())) {
                return false;
            }
        }

        return true;
    }
}