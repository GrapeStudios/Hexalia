package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.TeleportUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HomesteadBrewItem extends AbstractConsumableItem {
  public HomesteadBrewItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (!level.isClientSide && TeleportUtil.canReturn(level, player, true)) {
      return InteractionResultHolder.fail(player.getItemInHand(hand));
    }
    return super.use(level, player, hand);
  }

  @Override
  protected void handleEffects(Level level, LivingEntity user, ItemStack consumedStack) {
    if (user instanceof Player player) {
      TeleportUtil.teleportPlayerToSpawn(level, player, true);
      player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0, false, true, true));
    }
  }

  @Override
  protected ItemStack getReturnContainer(ItemStack consumedStack) {
    return new ItemStack(ModItems.RUSTIC_BOTTLE.get());
  }

  @Override
  protected Component getTooltip(ItemStack stack) {
    return Component.translatable("tooltip.hexalia.homestead_brew").withStyle(ChatFormatting.BLUE);
  }
}
