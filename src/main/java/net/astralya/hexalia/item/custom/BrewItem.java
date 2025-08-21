package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BrewItem extends Item {

    private final Supplier<StatusEffect> effectSupplier;
    private final int baseAmplifier;
    private final Text tooltip;

    public BrewItem(Settings settings, Supplier<StatusEffect> effectSupplier, int baseAmplifier, Text brewTooltip) {
        super(settings);
        this.effectSupplier = effectSupplier;
        this.baseAmplifier = baseAmplifier;
        this.tooltip = brewTooltip;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            StatusEffect effect = effectSupplier.get();
            if (effect != null) {
                int duration = Math.max(1, Configuration.get().brewEffectDuration);
                int amp = Math.max(0, baseAmplifier + Configuration.get().brewAmplifierBonus);
                user.addStatusEffect(new StatusEffectInstance(effect, duration, amp));
            }
        }

        if (stack.isEmpty()) {
            return new ItemStack(ModItems.RUSTIC_BOTTLE);
        }

        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            ItemStack bottle = new ItemStack(ModItems.RUSTIC_BOTTLE);
            stack.decrement(1);
            if (!player.getInventory().insertStack(bottle)) {
                player.dropItem(bottle, false);
            }
        }
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(this.tooltip);
    }
}
