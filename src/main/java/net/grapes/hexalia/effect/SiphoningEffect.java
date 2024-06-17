package net.grapes.hexalia.effect;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SiphoningEffect extends StatusEffect {
    protected final double modifier;

    protected SiphoningEffect(StatusEffectCategory category, int color, double modifier) {
        super(category, color);
        this.modifier = modifier;
    }

    // Handles Haste-like bonuses while this effect is active.
    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        return this.modifier * (double)(amplifier + 1);
    }

    // Causes the player to attract nearby items if not sneaking.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PlayerEntity player) || player.isSneaking()) {
            return;
        }

        World world = player.getWorld();
        double radius = 5.0 + amplifier;
        Box box = player.getBoundingBox().expand(radius);

        // Fetch nearby item entities efficiently
        List<ItemEntity> itemEntities = world.getEntitiesByClass(ItemEntity.class, box, item -> true);

        for (ItemEntity itemEntity : itemEntities) {
            if (player.getInventory().getEmptySlot() == -1) {
                Vec3d direction = player.getEyePos().subtract(itemEntity.getPos());
                int effectiveAmplifier = Math.min(amplifier + 1, 3);

                itemEntity.updatePosition(itemEntity.getX(), itemEntity.getY() + direction.y * 0.015 * effectiveAmplifier, itemEntity.getZ());

                if (world.isClient) {
                    itemEntity.prevY = itemEntity.getY();
                }

                itemEntity.setVelocity(itemEntity.getVelocity().multiply(0.95).add(direction.normalize().multiply(0.10 * effectiveAmplifier)));
            } else {
                itemEntity.onPlayerCollision(player);
            }
        }

        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

