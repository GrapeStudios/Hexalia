package net.grapes.hexalia.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;

public class KelpweaveBladeItem extends SwordItem {
    public KelpweaveBladeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // This method makes the blade to affect the target with the Slowness Effect on hit.
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0), attacker);
        return super.postHit(stack, target, attacker);
    }

    // This method allows the sword to repair itself while the player is either touching water or in the rain.
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player && stack.getDamage() > 0) {
            if (player.isTouchingWaterOrRain()) {
                float repairChance = 0.02F;
                if (world.random.nextFloat() < repairChance) {
                    stack.setDamage(stack.getDamage() - 1);
                }
            }
        }
    }
}

