package net.grapes.hexalia.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurifyingSaltsItem extends Item {

    public PurifyingSaltsItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BONE_MEAL_USE,
                SoundCategory.PLAYERS, 0.5f, 1.0f);

        if (!world.isClient) {
            List<net.minecraft.entity.effect.StatusEffect> effectsToRemove = new ArrayList<>();

            for (Map.Entry<net.minecraft.entity.effect.StatusEffect, StatusEffectInstance> entry : user.getActiveStatusEffects().entrySet()) {
                StatusEffectInstance effect = entry.getValue();
                if (effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                    effectsToRemove.add(effect.getEffectType());
                }
            }

            for (net.minecraft.entity.effect.StatusEffect effect : effectsToRemove) {
                user.removeStatusEffect(effect);
            }
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.hexalia.purifying_salts").formatted(Formatting.GRAY));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
}