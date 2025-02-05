package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class WeatherIdol extends Item {
    public WeatherIdol(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            if (world instanceof ServerWorld serverWorld) {
                if (stack.getItem() == ModItems.RAIN_IDOL) {
                    serverWorld.setWeather(0, 6000, true, false);
                    player.sendMessage(Text.translatable("message.hexalia.rain_idol"), true);
                } else if (stack.getItem() == ModItems.CLEAR_IDOL) {
                    serverWorld.setWeather(6000, 0, false, false);
                    player.sendMessage(Text.translatable("message.hexalia.clear_idol"), true);
                } else if (stack.getItem() == ModItems.STORM_IDOL) {
                    serverWorld.setWeather(0, 6000, true, true);
                    player.sendMessage(Text.translatable("message.hexalia.storm_idol"), true);
                }
            }

            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }

        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
