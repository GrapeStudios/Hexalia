package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BrewItem extends AbstractConsumableItem {

    private final int durationTicks;
    private final int baseAmplifier;
    private final Text baseTooltip;
    private final Supplier<StatusEffect> effectSupplier;

    public BrewItem(Settings settings,
                    Supplier<StatusEffect> effectSupplier,
                    int durationTicks,
                    int amplifier,
                    Text tooltip) {
        super(settings);
        this.effectSupplier = effectSupplier;
        this.durationTicks = Math.max(0, durationTicks);
        this.baseAmplifier = Math.max(0, amplifier);
        this.baseTooltip = tooltip;
    }

    @Override
    protected void handleEffects(World world, LivingEntity user, ItemStack consumedStack) {
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(effectSupplier.get(), durationTicks, baseAmplifier));
        }
    }

    @Override
    protected ItemStack getReturnContainer(ItemStack consumedStack) {
        return new ItemStack(ModItems.RUSTIC_BOTTLE);
    }

    @Override
    protected Text getTooltip(ItemStack stack) {
        return baseTooltip;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(baseTooltip);
    }
}