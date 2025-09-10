package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class MandrakeItem extends Item {
    public MandrakeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide && user instanceof Player player) {

            double radius = Configuration.MANDRAKE_SCREAM_RADIUS.get();
            int stunDuration = Configuration.MANDRAKE_STUN_DURATION.get();

            List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(radius));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity &&
                        !(player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.EARPLUGS.get()))) {
                    livingEntity.addEffect(new MobEffectInstance(ModMobEffects.STUNNED, stunDuration * 20, 0));
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.MANDRAKE_SCREAM.get(),
                    SoundSource.PLAYERS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.hexalia.mandrake").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
