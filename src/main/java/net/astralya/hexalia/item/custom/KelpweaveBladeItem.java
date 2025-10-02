package net.astralya.hexalia.item.custom;

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

    private static final float REPAIR_CHANCE = 0.05f;

    public KelpweaveBladeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0), attacker);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player && stack.getDamage() > 0) {
            if (isPlayerInWaterOrRain(player)) {
                attemptRepair(stack, world);
            }
        }
    }

    private boolean isPlayerInWaterOrRain(PlayerEntity player) {
        return player.isTouchingWaterOrRain();
    }

    private void attemptRepair(ItemStack stack, World world) {
        if (world.random.nextFloat() < REPAIR_CHANCE) {
            stack.setDamage(stack.getDamage() - 1);
        }
    }
}
