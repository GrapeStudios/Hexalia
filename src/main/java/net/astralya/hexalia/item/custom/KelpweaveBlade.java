package net.astralya.hexalia.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class KelpweaveBlade extends SwordItem {

    private static final float REPAIR_CHANCE = 0.05f;

    private static final int RIPTIDE_COOLDOWN_TICKS = 60;
    private static final int MIN_CHARGE_TICKS = 10;

    private static final int DURABILITY_COST = 1;

    public KelpweaveBlade(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0), player);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!isPlayerTouchingWater(player)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        if (!isPlayerTouchingWater(player)) {
            return;
        }

        int usedTicks = getUseDuration(stack, livingEntity) - timeCharged;
        if (usedTicks < MIN_CHARGE_TICKS) {
            return;
        }

        float charge = Math.min(usedTicks / 20.0F, 1.0F);

        float speed = 1.8F + (charge * 1.2F);
        float lift = 0.25F + (charge * 0.20F);

        if (!level.isClientSide) {
            Vec3 look = player.getLookAngle().normalize();
            Vec3 dash = new Vec3(look.x * speed, look.y * speed + lift, look.z * speed);

            player.setDeltaMovement(dash);
            player.hurtMarked = true;
            player.hasImpulse = true;
            player.fallDistance = 0.0F;

            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.TRIDENT_RIPTIDE_1.value(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            player.startAutoSpinAttack(20, 8.0F, stack);

            player.getCooldowns().addCooldown(this, RIPTIDE_COOLDOWN_TICKS);

            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(DURABILITY_COST, player, Player.getSlotForHand(player.getUsedItemHand()));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player && stack.getDamageValue() > 0) {
            if (isPlayerTouchingWater(player)) {
                attemptRepair(stack, level);
            }
        }
    }

    private boolean isPlayerTouchingWater(Player player) {
        return player.isInWaterOrRain();
    }

    private void attemptRepair(ItemStack stack, Level level) {
        if (level.random.nextFloat() < REPAIR_CHANCE) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }
}