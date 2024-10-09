package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.effect.ModMobEffects;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.sound.ModSounds;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MandrakeItem extends Item {
    public MandrakeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide && user instanceof Player player) {
            List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(5.0));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity &&
                        !(player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.EARPLUGS.get()))) {
                    livingEntity.addEffect(new MobEffectInstance(ModMobEffects.STUNNED.get(), 60, 0));
                }
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANDRAKE_SCREAM.get(),
                    SoundSource.PLAYERS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 16;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.mandrake").withStyle(ChatFormatting.GRAY));
    }
}
