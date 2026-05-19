package net.astralya.hexalia.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class PurityIdolItem extends WeatherIdolItem {
  public PurityIdolItem(Properties properties) {
    super(properties);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
    if (!level.isClientSide) {
      ItemStack main = user.getMainHandItem();
      ItemStack off = user.getOffhandItem();
      ItemStack target = stack == main ? off : main;

      boolean removed = removeCurses(target);

      if (removed) {
        level.playSound(
            null,
            user.getX(),
            user.getY(),
            user.getZ(),
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.PLAYERS,
            1.0F,
            1.2F);
        if (user instanceof Player player && !player.getAbilities().instabuild) {
          stack.shrink(1);
        }
      }
    }

    if (user instanceof ServerPlayer serverPlayer) {
      serverPlayer.awardStat(Stats.ITEM_USED.get(this));
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
    }

    return stack;
  }

  private boolean removeCurses(ItemStack target) {
    if (target.isEmpty()) {
      return false;
    }

    ItemEnchantments enchantments =
        target.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
    if (enchantments.isEmpty()) {
      return false;
    }

    ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(enchantments);
    int before = mutableEnchantments.keySet().size();
    mutableEnchantments.removeIf(holder -> holder.is(EnchantmentTags.CURSE));
    int after = mutableEnchantments.keySet().size();

    if (after < before) {
      target.set(DataComponents.ENCHANTMENTS, mutableEnchantments.toImmutable());
      return true;
    }
    return false;
  }
}
