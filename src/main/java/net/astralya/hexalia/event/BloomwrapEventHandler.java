package net.astralya.hexalia.event;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BloomwrapEventHandler {

    private static final float KNOCKBACK_REDUCTION = 0.8f;
    private static final int   EFFECT_DURATION     = 60;
    private static final double FLOWER_SCAN_RADIUS = 4.0;

    public static void onPlayerTick(PlayerEntity player) {
        if (player.getWorld().isClient) return;
        if ((player.age % 20) != 0) return;
        tickBoots(player);
        tickLeggings(player);
    }

    public static boolean onKnockback(LivingEntity entity, double strength) {
        if (entity.getWorld().isClient) return false;
        if (!isWearing(entity, EquipmentSlot.HEAD, ModItems.BLOOMWRAP_HAT)) return false;
        return true;
    }

    public static float getReducedKnockback(float strength) {
        return strength * (1.0f - KNOCKBACK_REDUCTION);
    }

    private static void tickBoots(PlayerEntity player) {
        if (!isWearing(player, EquipmentSlot.FEET, ModItems.BLOOMWRAP_BOOTS)) return;
        BlockPos below = player.getBlockPos().down();
        World world = player.getWorld();
        boolean onNature = world.getBlockState(below).isIn(BlockTags.DIRT)
                || world.getBlockState(below).isIn(BlockTags.LEAVES)
                || world.getBlockState(below).isIn(BlockTags.SMALL_FLOWERS)
                || world.getBlockState(below).isIn(BlockTags.TALL_FLOWERS);
        if (onNature) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, EFFECT_DURATION, 0, false, false, true));
        }
    }

    private static void tickLeggings(PlayerEntity player) {
        if (!isWearing(player, EquipmentSlot.LEGS, ModItems.BLOOMWRAP_LEGGINGS)) return;
        World world = player.getWorld();
        BlockPos origin = player.getBlockPos();
        Box scanArea = new Box(origin).expand(FLOWER_SCAN_RADIUS);
        boolean nearFlower = BlockPos.stream(
                        BlockPos.ofFloored(scanArea.minX, scanArea.minY, scanArea.minZ),
                        BlockPos.ofFloored(scanArea.maxX, scanArea.maxY, scanArea.maxZ))
                .anyMatch(p -> world.getBlockState(p).isIn(BlockTags.SMALL_FLOWERS)
                        || world.getBlockState(p).isIn(BlockTags.TALL_FLOWERS));
        if (nearFlower) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, EFFECT_DURATION, 0, false, false, true));
        }
    }

    private static boolean isWearing(LivingEntity entity, EquipmentSlot slot, Item item) {
        ItemStack stack = entity.getEquippedStack(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }
}