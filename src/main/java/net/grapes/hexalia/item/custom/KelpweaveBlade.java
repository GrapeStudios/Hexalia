package net.grapes.hexalia.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public class KelpweaveBlade extends SwordItem {

    private static final float REPAIR_CHANCE = 0.05f;

    public KelpweaveBlade(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    100, 0), player);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide() && pEntity instanceof Player player && pStack.getDamageValue() > 0) {
            if (isPlayerTouchingWater(player)) {
                attemptRepair(pStack, pLevel);
            }
        }
    }


    private boolean isPlayerTouchingWater(Player player) {
        return player.isInWaterOrRain();
    }


    private void attemptRepair(ItemStack pStack, Level pLevel) {
        if (pLevel.random.nextFloat() < REPAIR_CHANCE) {
            pStack.setDamageValue(pStack.getDamageValue() - 1);
        }
    }
}
