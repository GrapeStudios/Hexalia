package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            List<Entity> entities = world.getOtherEntities(player, player.getBoundingBox().expand(Configuration.MANDRAKE_SCREAM_RADIUS.get()));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity &&
                        !(player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModTags.Items.STUN_IMMUNE_HEADWEAR) && !(player.isCreative()))) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(ModMobEffects.STUNNED, Configuration.MANDRAKE_STUN_DURATION.get(), 0));
                }
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.MANDRAKE_SCREAM,
                    SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.hexalia.mandrake").formatted(Formatting.GRAY));
    }
}
