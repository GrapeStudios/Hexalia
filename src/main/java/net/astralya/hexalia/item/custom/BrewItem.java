package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class BrewItem extends AbstractConsumableItem {

    private static final Identifier MOONWEAVE_SET_ID = Identifier.of("hexalia", "moonweave");
    private static final float MOONWEAVE_DURATION_MULTIPLIER = 1.5f;

    private final int durationTicks;
    private final int baseAmplifier;
    private final Text baseTooltip;
    private final Supplier<RegistryEntry<StatusEffect>> effectSupplier;

    public BrewItem(Settings settings,
                    Supplier<RegistryEntry<StatusEffect>> effectSupplier,
                    int durationTicks,
                    int amplifier,
                    Text tooltip) {
        super(settings);
        this.effectSupplier = effectSupplier;
        this.durationTicks = Math.max(0, durationTicks);
        this.baseAmplifier = Math.max(0, amplifier);
        this.baseTooltip = tooltip;
    }

    public int getBrewColor() {
        return effectSupplier.get().value().getColor();
    }

    @Override
    protected void handleEffects(World world, LivingEntity user, ItemStack consumedStack) {
        if (world.isClient) return;
        int duration = isWearingFullMoonweaveSet(user)
                ? Math.round(durationTicks * MOONWEAVE_DURATION_MULTIPLIER)
                : durationTicks;
        user.addStatusEffect(new StatusEffectInstance(effectSupplier.get(), duration, baseAmplifier));
    }

    private static boolean isWearingFullMoonweaveSet(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[]{ EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
            ItemStack piece = entity.getEquippedStack(slot);
            Identifier id = piece.get(ModComponents.ARMOR_SET_ID);
            if (!MOONWEAVE_SET_ID.equals(id)) return false;
        }
        return true;
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
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(baseTooltip);
    }
}