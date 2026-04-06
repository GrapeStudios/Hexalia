package net.astralya.hexalia.util;

import net.astralya.hexalia.component.ModComponents;
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
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT);
        return pct != null && pct > 0.0f;
    }

    public static void addPieceLine(ItemStack stack, List<Text> lines) {
        Float pct = stack.get(ModComponents.MAGIC_RESIST_PCT);
        if (pct == null || pct <= 0.0f) return;
        lines.add(Text.translatable("tooltip.hexalia.magic_resist_piece", formatPct(pct))
                .formatted(Formatting.DARK_GREEN));
    }

    public static void addFullSetLineIfWorn(LivingEntity entity, ItemStack hoveredStack, List<Text> lines) {
        Identifier groupId = hoveredStack.get(ModComponents.ARMOR_SET_GROUP_ID);
        if (groupId == null) return;
        if (!MagicResistanceHelper.isWearingFullSetGroup(entity, groupId)) return;
        float bonus = MagicResistanceHelper.getGroupBonusForEntity(entity, groupId);
        if (bonus <= 0.0f) return;
        lines.add(Text.translatable("tooltip.hexalia.magic_resist_full_set", formatPct(bonus))
                .formatted(Formatting.GREEN));
    }

    private static String formatPct(float pct) {
        return Math.round(pct * 100.0f) + "%";
    }
}