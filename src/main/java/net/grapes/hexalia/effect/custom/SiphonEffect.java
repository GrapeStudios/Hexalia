package net.grapes.hexalia.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SiphonEffect extends MobEffect {
    protected final double modifier;

    public SiphonEffect(MobEffectCategory mobEffectCategory, int color, double modifier) {
        super(mobEffectCategory, color);
        this.modifier = modifier;
    }

    // Handles Haste-like bonuses while this effect is active.
    public double adjustModifierAmount(int amplifier, AttributeModifier modifier) {
        return this.modifier * (double)(amplifier + 1);
    }

    // Causes the player to attract nearby items if not sneaking.
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!(pLivingEntity instanceof Player player) || player.isCrouching()) {
            return;
        }

        Level world = player.level();
        double radius = 5.0 + pAmplifier;
        AABB box = player.getBoundingBox().inflate(radius);

        // Fetch nearby item entities efficiently
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, box, item -> true);

        for (ItemEntity itemEntity : itemEntities) {
            if (player.getInventory().getFreeSlot() == -1) {
                Vec3 direction = player.getEyePosition().subtract(itemEntity.position());
                int effectiveAmplifier = Math.min(pAmplifier + 1, 3);

                itemEntity.setPos(itemEntity.getX(), itemEntity.getY() + direction.y * 0.015 * effectiveAmplifier, itemEntity.getZ());

                if (world.isClientSide) {
                    itemEntity.yOld = itemEntity.getY();
                }

                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().scale(0.95).add(direction.normalize().scale(0.10 * effectiveAmplifier)));
            } else {
                itemEntity.playerTouch(player);
            }
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
