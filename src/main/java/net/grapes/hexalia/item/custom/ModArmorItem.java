package net.grapes.hexalia.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModArmorItem extends ArmorItem {

    private int effectRemovalTimer = 0;

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (!world.isClient()) {
                if (hasChestAndLeggingsOn(player)) {
                    applyEffect(player, StatusEffects.WATER_BREATHING);
                    removeEffect(player, StatusEffects.POISON);
                    effectRemovalTimer = 0;
                } else {
                    if (effectRemovalTimer < 500) {
                        effectRemovalTimer++;
                    } else {
                        player.removeStatusEffect(StatusEffects.WATER_BREATHING);
                    }
                }

                if (hasHelmetAndBootsOn(player)) {
                    applyEffect(player, StatusEffects.NIGHT_VISION);
                    applyEffect(player, StatusEffects.DOLPHINS_GRACE);
                    effectRemovalTimer = 0;
                } else {
                    if (effectRemovalTimer < 500) {
                        effectRemovalTimer++;
                    } else {
                        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                        player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
                    }
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean hasChestAndLeggingsOn(PlayerEntity player) {
        ItemStack chestplate = player.getInventory().getArmorStack(2);
        ItemStack leggings = player.getInventory().getArmorStack(1);

        return !chestplate.isEmpty() && !leggings.isEmpty();
    }

    private boolean hasHelmetAndBootsOn(PlayerEntity player) {
        ItemStack helmet = player.getInventory().getArmorStack(3);
        ItemStack boots = player.getInventory().getArmorStack(0);

        return !helmet.isEmpty() && !boots.isEmpty();
    }

    private void applyEffect(PlayerEntity player, StatusEffect effectType) {
        int duration = 300;

        if (!player.hasStatusEffect(effectType) || player.getStatusEffect(effectType).getDuration() <= 220) {
            player.addStatusEffect(new StatusEffectInstance(effectType, duration, 0, false, false, true));
        }
    }

    private void removeEffect(PlayerEntity player, StatusEffect effectType) {
        if (player.hasStatusEffect(effectType)) {
            player.removeStatusEffect(effectType);
        }
    }
}
