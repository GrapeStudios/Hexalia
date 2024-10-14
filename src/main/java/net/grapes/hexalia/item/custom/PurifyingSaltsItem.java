package net.grapes.hexalia.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurifyingSaltsItem extends Item {
    public PurifyingSaltsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level level, LivingEntity user) {
        if (user instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, pStack);
        }

        if (user instanceof Player player && !player.getAbilities().instabuild) {
            pStack.shrink(1);
        }

        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BONE_MEAL_USE,
                SoundSource.PLAYERS, 0.5f, 1.0f);

        if (!level.isClientSide) {
            List<MobEffect> effectsToRemove = new ArrayList<>();

            for (Map.Entry<MobEffect, MobEffectInstance> entry : user.getActiveEffectsMap().entrySet()) {
                MobEffectInstance effect = entry.getValue();
                if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    effectsToRemove.add(effect.getEffect());
                }
            }

            for (MobEffect effect : effectsToRemove) {
                user.removeEffect(effect);
            }
        }

        return pStack.isEmpty() ? ItemStack.EMPTY : pStack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.purifying_salts").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }
}
