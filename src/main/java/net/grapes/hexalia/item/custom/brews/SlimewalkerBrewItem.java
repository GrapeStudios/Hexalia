package net.grapes.hexalia.item.custom.brews;

import net.grapes.hexalia.effect.ModMobEffects;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimewalkerBrewItem extends Item {
    public SlimewalkerBrewItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);

        if (user instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (!world.isClientSide) {
            user.addEffect(new MobEffectInstance(ModMobEffects.SLIMEWALKER.get(), 2400));
        }

        if (stack.isEmpty()) {
            return new ItemStack(ModItems.RUSTIC_BOTTLE.get());
        }

        if (user instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack itemStack = new ItemStack(ModItems.RUSTIC_BOTTLE.get());
            stack.shrink(1);
            if (!player.getInventory().add(itemStack)) {
                player.drop(itemStack, false);
            }
        }

        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.slimewalker_brew").withStyle(ChatFormatting.BLUE));
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.slimewalker_brew_2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }
}
