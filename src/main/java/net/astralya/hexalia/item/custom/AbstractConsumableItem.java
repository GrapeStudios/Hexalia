package net.astralya.hexalia.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
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

public abstract class AbstractConsumableItem extends Item {

    public AbstractConsumableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack original = stack.copy();
        ItemStack current = super.finishUsing(stack, world, user);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, original);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        handleEffects(world, user, original);

        ItemStack container = getReturnContainer(original);

        if (!world.isClient && user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            boolean isFood = original.getItem().isFood();
            if (!isFood) {
                current.decrement(1);
            }

            if (current.isEmpty()) {
                return container;
            }
            if (!container.isEmpty()) {
                if (!player.getInventory().insertStack(container)) {
                    player.dropItem(container, false);
                }
            }
        }

        return current;
    }

    protected abstract void handleEffects(World world, LivingEntity user, ItemStack consumedStack);

    protected abstract ItemStack getReturnContainer(ItemStack consumedStack);

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return ItemUsage.consumeHeldItem(world, player, hand);
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
        Text line = getTooltip(stack);
        if (line != null) tooltip.add(line);
    }

    protected Text getTooltip(ItemStack stack) { return null; }
}
