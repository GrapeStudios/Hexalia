package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class MagicResistanceTooltip {
    private MagicResistanceTooltip() {
    }

    public static boolean hasMagicResist(ItemStack stack) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT.get());
        return pct != null && pct > 0.0f;
    }

    public static void addPieceLine(ItemStack stack, List<Component> lines) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT.get());
        if (pct == null || pct <= 0.0f) {
            return;
        }

        lines.add(Component.translatable("tooltip.hexalia.magic_resist_piece", formatPct(pct))
                .withStyle(ChatFormatting.DARK_GREEN));
    }

    public static void addFullSetLineIfWorn(LivingEntity entity, ItemStack hoveredStack, List<Component> lines) {
        ResourceLocation setId = hoveredStack.get(ModComponents.ARMOR_SET_ID.get());
        if (setId == null) {
            return;
        }

        if (!isWearingFullSet(entity, setId)) {
            return;
        }

        float bonus = getFullSetBonusFromAnyPiece(entity, setId);
        if (bonus <= 0.0f) {
            return;
        }

        lines.add(Component.translatable("tooltip.hexalia.magic_resist_full_set", formatPct(bonus))
                .withStyle(ChatFormatting.GREEN));
    }

    private static boolean isWearingFullSet(LivingEntity entity, ResourceLocation setId) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);

        return matchesSet(head, setId) && matchesSet(chest, setId) && matchesSet(legs, setId) && matchesSet(feet, setId);
    }

    private static boolean matchesSet(ItemStack stack, ResourceLocation setId) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation id = stack.get(ModComponents.ARMOR_SET_ID.get());
        return id != null && id.equals(setId);
    }

    private static float getFullSetBonusFromAnyPiece(LivingEntity entity, ResourceLocation setId) {
        float bonus = 0.0f;

        bonus = Math.max(bonus, getBonus(entity.getItemBySlot(EquipmentSlot.HEAD), setId));
        bonus = Math.max(bonus, getBonus(entity.getItemBySlot(EquipmentSlot.CHEST), setId));
        bonus = Math.max(bonus, getBonus(entity.getItemBySlot(EquipmentSlot.LEGS), setId));
        bonus = Math.max(bonus, getBonus(entity.getItemBySlot(EquipmentSlot.FEET), setId));

        return bonus;
    }

    private static float getBonus(ItemStack stack, ResourceLocation setId) {
        ResourceLocation id = stack.get(ModComponents.ARMOR_SET_ID.get());
        if (id == null || !id.equals(setId)) {
            return 0.0f;
        }

        Float bonus = stack.get(ModComponents.FULL_SET_BONUS_PCT.get());
        return bonus != null ? bonus : 0.0f;
    }

    private static String formatPct(float pct) {
        int v = Math.round(pct * 100.0f);
        return v + "%";
    }
}