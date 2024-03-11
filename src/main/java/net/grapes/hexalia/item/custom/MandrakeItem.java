package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class MandrakeItem extends Item {
    public MandrakeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            List<Entity> entities = world.getOtherEntities(player, player.getBoundingBox().expand(6.0));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STUNNED, 160, 0));
                }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_WET_GRASS_HIT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if(!player.isCreative()) {
                stack.decrement(1);
            }
        }
        return stack;
    }
        return stack;
    }
}
