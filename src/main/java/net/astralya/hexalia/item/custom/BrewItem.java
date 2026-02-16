package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class BrewItem extends AbstractConsumableItem {

    private final int durationTicks;
    private final int baseAmplifier;
    private final Component baseTooltip;
    private final Supplier<Holder<MobEffect>> effectSupplier;

    public BrewItem(Properties properties,
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
        MobEffect effect = effectSupplier.get().value();
        return effect.getColor();
    }

    @Override
    protected void handleEffects(Level level, LivingEntity user, ItemStack consumedStack) {
        if (!level.isClientSide) {
            user.addEffect(new MobEffectInstance(effectSupplier.get(), durationTicks, baseAmplifier));
        }
    }

    @Override
    protected ItemStack getReturnContainer(ItemStack consumedStack) {
        return new ItemStack(ModItems.RUSTIC_BOTTLE.get());
    }

    @Override
    protected Component getTooltip(ItemStack stack) {
        return baseTooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(baseTooltip);
    }
}
