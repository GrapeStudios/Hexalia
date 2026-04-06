package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.entity.custom.projectile.PurifyingSacProjectile;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class PurifyingSacItem extends Item {

    public PurifyingSacItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.isSneaking()) {
            if (!world.isClient) {
                PurifyingSacProjectile proj = new PurifyingSacProjectile(world, player);
                proj.setItem(stack.copyWithCount(1));
                proj.setVelocity(player, player.getPitch(), player.getYaw(), -20.0F, 0.5F, 1.0F);
                world.spawnEntity(proj);

                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS,
                        0.5F, 0.8F + world.random.nextFloat() * 0.4F);

                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            return TypedActionResult.success(stack, world.isClient);
        }

        return ItemUsage.consumeHeldItem(world, player, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity sp) {
            sp.incrementStat(Stats.USED.getOrCreateStat(this));
            Criteria.CONSUME_ITEM.trigger(sp, stack);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.PLAYERS, 0.5F, 1.0F);

        if (!world.isClient) {
            ModUtil.removeHarmfulEffects(user);
        }

        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode && stack.isDamageable()) {
            EquipmentSlot slot = (stack == player.getOffHandStack()) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.damage(1, player, slot);

            if (stack.isEmpty()) {
                player.setStackInHand((slot == EquipmentSlot.OFFHAND) ? Hand.OFF_HAND : Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        return stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.hexalia.purifying_sac").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.hexalia.throwable").formatted(Formatting.GRAY, Formatting.ITALIC));
    }
}