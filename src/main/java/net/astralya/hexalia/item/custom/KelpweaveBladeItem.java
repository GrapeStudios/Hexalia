package net.astralya.hexalia.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class KelpweaveBladeItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 3;
    private static final float ATTACK_SPEED = -2.4F;

    private static final float REPAIR_CHANCE = 0.05f;
    private static final int RIPTIDE_COOLDOWN_TICKS = 60;
    private static final int MIN_CHARGE_TICKS = 10;
    private static final int DURABILITY_COST = 1;

    public KelpweaveBladeItem(ToolMaterial material, Settings settings) {
        super(material, ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 0), player);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }
        if (!player.isTouchingWaterOrRain()) {
            return TypedActionResult.pass(stack);
        }
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity livingEntity, int remainingUseTicks) {
        if (!(livingEntity instanceof PlayerEntity player)) {
            return;
        }
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return;
        }
        if (!player.isTouchingWaterOrRain()) {
            return;
        }

        int usedTicks = getMaxUseTime(stack) - remainingUseTicks;
        if (usedTicks < MIN_CHARGE_TICKS) {
            return;
        }

        float charge = Math.min(usedTicks / 20.0F, 1.0F);
        float speed = 1.8F + (charge * 1.2F);
        float lift = 0.25F + (charge * 0.20F);

        if (!world.isClient) {
            Vec3d look = player.getRotationVec(1.0F).normalize();
            Vec3d dash = new Vec3d(look.x * speed, look.y * speed + lift, look.z * speed);
            player.setVelocity(dash);
            player.velocityModified = true;
            player.fallDistance = 0.0F;
            world.playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ITEM_TRIDENT_RIPTIDE_1,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
            );
            player.getItemCooldownManager().set(this, RIPTIDE_COOLDOWN_TICKS);
            if (!player.getAbilities().creativeMode) {
                stack.damage(DURABILITY_COST, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slotId, boolean isSelected) {
        if (!world.isClient && entity instanceof PlayerEntity player && stack.getDamage() > 0) {
            if (player.isTouchingWaterOrRain()) {
                attemptRepair(stack, world);
            }
        }
    }

    private void attemptRepair(ItemStack stack, World world) {
        if (world.random.nextFloat() < REPAIR_CHANCE) {
            stack.setDamage(stack.getDamage() - 1);
        }
    }
}