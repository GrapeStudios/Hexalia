package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.TeleportUtil;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class HomesteadBrewItem extends Item {
    public HomesteadBrewItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && TeleportUtil.canReturn(world, user, true)) {
            return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        PlayerEntity player = (user instanceof PlayerEntity) ? (PlayerEntity) user : null;

        if (player != null) {
            stack.decrementUnlessCreative(1, player);
        }

        if (player != null && !(player instanceof FakePlayer)) {
            TeleportUtil.teleportPlayerToSpawn(world, player, true);
        }

        ItemStack bottle = new ItemStack(ModItems.RUSTIC_BOTTLE);
        if (player == null || !player.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return bottle;
            } else if (!player.getInventory().insertStack(bottle)) {
                player.dropItem(bottle, false);
            }
        }

        return result;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.hexalia.homestead_brew").formatted(Formatting.BLUE));
    }
}