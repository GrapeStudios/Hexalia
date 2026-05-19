package net.astralya.hexalia.item.custom;

import java.util.function.Supplier;
import net.astralya.hexalia.Hexalia;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrewItem extends AbstractConsumableItem {
  private static final ResourceLocation MOONWEAVE_SET_ID =
      ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "moonweave");
  private static final float MOONWEAVE_DURATION_MULTIPLIER = 1.5F;

  private final int durationTicks;
  private final int baseAmplifier;
  private final Component baseTooltip;
  private final Supplier<Holder<MobEffect>> effectSupplier;

  public BrewItem(
      Properties properties,
      Supplier<Holder<MobEffect>> effectSupplier,
      int durationTicks,
      int amplifier,
      Component tooltip) {
    super(properties);
    this.effectSupplier = effectSupplier;
    this.durationTicks = Math.max(0, durationTicks);
    this.baseAmplifier = Math.max(0, amplifier);
    this.baseTooltip = tooltip;
  }

  public int getBrewColor() {
    return effectSupplier.get().value().getColor();
  }

  @Override
  protected void handleEffects(Level level, LivingEntity user, ItemStack consumedStack) {
    if (!level.isClientSide) {
      int duration =
          isWearingFullMoonweaveSet(user)
              ? Math.round(durationTicks * MOONWEAVE_DURATION_MULTIPLIER)
              : durationTicks;
      user.addEffect(new MobEffectInstance(boundEffect(), duration, baseAmplifier));
    }
  }

  private static boolean isWearingFullMoonweaveSet(LivingEntity entity) {
    return isMoonweavePiece(entity, EquipmentSlot.HEAD)
        && isMoonweavePiece(entity, EquipmentSlot.CHEST)
        && isMoonweavePiece(entity, EquipmentSlot.LEGS)
        && isMoonweavePiece(entity, EquipmentSlot.FEET);
  }

  private static boolean isMoonweavePiece(LivingEntity entity, EquipmentSlot slot) {
    return MOONWEAVE_SET_ID.equals(
        entity.getItemBySlot(slot).get(ModComponents.ARMOR_SET_ID.get()));
  }

  private Holder<MobEffect> boundEffect() {
    MobEffect effect = effectSupplier.get().value();
    return BuiltInRegistries.MOB_EFFECT
        .getResourceKey(effect)
        .map(BuiltInRegistries.MOB_EFFECT::getHolderOrThrow)
        .orElseThrow(() -> new IllegalStateException("Unregistered mob effect: " + effect));
  }

  @Override
  protected ItemStack getReturnContainer(ItemStack consumedStack) {
    return new ItemStack(ModItems.RUSTIC_BOTTLE.get());
  }

  @Override
  protected Component getTooltip(ItemStack stack) {
    return baseTooltip;
  }
}
