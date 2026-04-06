package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class SalveItem extends AbstractConsumableItem {

    private final Supplier<RegistryEntry<StatusEffect>> effectSupplier;
    private final int durationTicks;
    private final int baseAmplifier;
    private final int useDurationTicks;
    private final Text baseTooltip;

    public SalveItem(Settings settings,
                     Supplier<RegistryEntry<StatusEffect>> effectSupplier,
                     int durationTicks,
                     int amplifier,
                     int useDurationTicks,
                     Text tooltip) {
        super(settings);
        this.effectSupplier = effectSupplier;
        this.durationTicks = Math.max(0, durationTicks);
        this.baseAmplifier = Math.max(0, amplifier);
        this.useDurationTicks = Math.max(1, useDurationTicks);
        this.baseTooltip = tooltip;
    }

    @Override
    protected void handleEffects(World world, LivingEntity user, ItemStack consumedStack) {
        if (world.isClient) {
            return;
        }
        user.removeStatusEffect(ModMobEffects.BLEEDING);
        user.addStatusEffect(new StatusEffectInstance(effectSupplier.get(), durationTicks, baseAmplifier));
    }

    @Override
    protected ItemStack getReturnContainer(ItemStack consumedStack) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return useDurationTicks;
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    protected Text getTooltip(ItemStack stack) {
        return baseTooltip;
    }
}