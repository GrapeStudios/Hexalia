package net.grapes.hexalia.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;

public class ModEventHandler {

    public static void registerServerEvents() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (PlayerEntity player : world.getPlayers()) {
                applyArmorEffects(player);
            }
        });
    }

    private static void applyArmorEffects(PlayerEntity player) {
        World world = player.getWorld();

        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

        boolean isWearingFullSet = helmet.getItem() instanceof ArmorItem &&
                chestplate.getItem() instanceof ArmorItem &&
                leggings.getItem() instanceof ArmorItem &&
                boots.getItem() instanceof ArmorItem;

        if (helmet.getItem() instanceof ArmorItem && player.isSubmergedInWater()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false, true));
        }

        if (chestplate.getItem() instanceof ArmorItem && player.isSubmergedInWater()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 220, 0, false, false, true));
        }

        if (leggings.getItem() instanceof ArmorItem) {
            if (player.hasStatusEffect(StatusEffects.POISON)) {
                player.removeStatusEffect(StatusEffects.POISON);
            }
        }

        if (boots.getItem() instanceof ArmorItem && player.isSubmergedInWater()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 220, 0, false, false, true));
        }

        if (isWearingFullSet) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 220, 0, false, false, true));
        }
    }
}
