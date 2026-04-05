package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BrewItem extends AbstractConsumableItem {

    private static final Identifier MOONWEAVE_SET_ID = new Identifier("hexalia", "moonweave");
    private static final String ARMOR_SET_ID_KEY = "ArmorSetId";
    private static final float MOONWEAVE_DURATION_MULTIPLIER = 1.5F;

    private final int durationTicks;
    private final int baseAmplifier;
    private final Text baseTooltip;
    private final Supplier<StatusEffect> effectSupplier;

    public BrewItem(Settings settings, Supplier<StatusEffect> effectSupplier, int durationTicks, int amplifier, Text tooltip) {
        super(settings);
        this.effectSupplier = effectSupplier;
        this.durationTicks = Math.max(0, durationTicks);
        this.baseAmplifier = Math.max(0, amplifier);
        this.baseTooltip = tooltip;
    }

    public int getBrewColor() {
        return effectSupplier.get().getColor();
    }

    @Override
    protected void handleEffects(World world, LivingEntity user, ItemStack consumedStack) {
        if (world.isClient) {
            return;
        }

        int duration = isWearingFullMoonweaveSet(user) ? Math.round(durationTicks * MOONWEAVE_DURATION_MULTIPLIER) : durationTicks;
        user.addStatusEffect(new StatusEffectInstance(effectSupplier.get(), duration, baseAmplifier));
    }

    private static boolean isWearingFullMoonweaveSet(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack piece = entity.getEquippedStack(slot);
            NbtCompound nbt = piece.getNbt();
            if (nbt == null || !nbt.contains(ARMOR_SET_ID_KEY)) {
                return false;
            }

            if (!MOONWEAVE_SET_ID.toString().equals(nbt.getString(ARMOR_SET_ID_KEY))) {
                return false;
            }
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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(baseTooltip);
    }
}