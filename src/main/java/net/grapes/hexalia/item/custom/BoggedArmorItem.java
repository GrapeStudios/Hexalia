package net.grapes.hexalia.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BoggedArmorItem extends ArmorItem {

    private int waterBreathingTimer = 0;
    private int poisonRemovalTimer = 0;

    public BoggedArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (!world.isClient()) {
                if (isWearingFullArmor(player)) {
                    applyWaterBreathingEffect(player);
                    removePoisonEffect(player);
                    resetTimers();
                } else {
                    handleEffectRemoval(player);
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean isWearingFullArmor(PlayerEntity player) {
        for (int i = 0; i < 4; i++) {
            if (player.getInventory().getArmorStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void applyWaterBreathingEffect(PlayerEntity player) {
        int duration = 300;
        if (!player.hasStatusEffect(StatusEffects.WATER_BREATHING) ||
                Objects.requireNonNull(player.getStatusEffect(StatusEffects.WATER_BREATHING)).getDuration() <= 220) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, duration, 0, false, false, true));
        }
    }

    private void removePoisonEffect(PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            player.removeStatusEffect(StatusEffects.POISON);
            player.heal(0.1f);
        }
    }

    private void resetTimers() {
        waterBreathingTimer = 0;
        poisonRemovalTimer = 0;
    }

    private void handleEffectRemoval(PlayerEntity player) {
        if (waterBreathingTimer < 500) {
            waterBreathingTimer++;
        } else {
            player.removeStatusEffect(StatusEffects.WATER_BREATHING);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.hexalia.bogged_armor").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("tooltip.hexalia.bogged_armor_2").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.hexalia.hold_shift").formatted(Formatting.GRAY));
        }
    }
}
