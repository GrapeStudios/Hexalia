package net.astralya.hexalia.util;

import net.astralya.hexalia.item.custom.armor.MagicResistanceArmor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public final class MagicResistanceTooltip {

    private MagicResistanceTooltip() {
    }

    public static boolean hasMagicResist(ItemStack stack) {
        return stack.getItem() instanceof MagicResistanceArmor armor && armor.getMagicResistanceBonus() > 0.0F;
    }

    public static void addPieceLine(ItemStack stack, List<Text> lines) {
        if (!(stack.getItem() instanceof MagicResistanceArmor armor)) {
            return;
        }

        float pct = armor.getMagicResistanceBonus();
        if (pct <= 0.0F) {
            return;
        }

        lines.add(Text.translatable("tooltip.hexalia.magic_resist_piece", formatPct(pct)).formatted(Formatting.DARK_GREEN));
    }

    public static void addFullSetLineIfWorn(LivingEntity entity, ItemStack hoveredStack, List<Text> lines) {
        if (!(hoveredStack.getItem() instanceof MagicResistanceArmor armor)) {
            return;
        }

        Identifier groupId = armor.getArmorSetGroupId();
        if (!MagicResistanceHelper.isWearingFullSetGroup(entity, groupId)) {
            return;
        }

        float bonus = MagicResistanceHelper.getGroupBonusForEntity(entity, groupId);
        if (bonus <= 0.0F) {
            return;
        }

        lines.add(Text.translatable("tooltip.hexalia.magic_resist_full_set", formatPct(bonus)).formatted(Formatting.GREEN));
    }

    private static String formatPct(float pct) {
        return Math.round(pct * 100.0F) + "%";
    }
}