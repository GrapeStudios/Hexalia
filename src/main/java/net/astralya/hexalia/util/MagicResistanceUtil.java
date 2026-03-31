package net.astralya.hexalia.util;

import net.astralya.hexalia.item.custom.armor.MagicResistanceArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MagicResistanceUtil {
    public static final ResourceLocation SILKWEAVE_SET_ID = new ResourceLocation("hexalia", "silkweave");
    public static final float FULL_SET_BONUS = 0.10f;

    private MagicResistanceUtil() {
    }

    public static float getTotalMagicResistance(LivingEntity entity) {
        float total = 0.0f;

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof MagicResistanceArmor armor) {
                total += armor.getMagicResistanceBonus();
            }
        }

        if (isWearingFullSet(entity, SILKWEAVE_SET_ID)) {
            total += FULL_SET_BONUS;
        }

        return total;
    }

    public static boolean isWearingFullSet(LivingEntity entity, ResourceLocation setId) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof MagicResistanceArmor armor)) {
                return false;
            }
            if (!setId.equals(armor.getArmorSetId())) {
                return false;
            }
        }

        return true;
    }
}