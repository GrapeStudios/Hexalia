package net.astralya.hexalia.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractConsumableItem extends Item {

    public AbstractConsumableItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack original = stack.copy();
        ItemStack current = super.finishUsingItem(stack, level, user);

        if (user instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, original);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        handleEffects(level, user, original);

        ItemStack container = getReturnContainer(original);

        if (!level.isClientSide && user instanceof Player player && !player.getAbilities().instabuild) {
            boolean isFood = original.get(DataComponents.FOOD) != null;
            if (!isFood) current.shrink(1);

            if (current.isEmpty()) {
                return container;
            }
            if (!container.isEmpty()) {
                if (!player.getInventory().add(container)) {
                    player.drop(container, false);
                }
            }
        }

        return current;
    }

    protected abstract void handleEffects(Level level, LivingEntity user, ItemStack consumedStack);

    protected abstract ItemStack getReturnContainer(ItemStack consumedStack);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Component line = getTooltip(stack);
        if (line != null) tooltip.add(line);
    }

    protected Component getTooltip(ItemStack stack) { return null; }
}
