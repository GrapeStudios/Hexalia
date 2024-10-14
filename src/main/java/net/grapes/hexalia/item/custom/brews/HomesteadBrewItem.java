package net.grapes.hexalia.item.custom.brews;

import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.TeleportUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HomesteadBrewItem extends Item {
    public HomesteadBrewItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide && TeleportUtil.canReturn(world, player, true)) {
            return new InteractionResultHolder<>(net.minecraft.world.InteractionResult.FAIL, player.getItemInHand(hand));
        }
        player.startUsingItem(hand);
        return new InteractionResultHolder<>(net.minecraft.world.InteractionResult.SUCCESS, player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (player != null && !(player instanceof FakePlayer)) {
            TeleportUtil.teleportPlayerToSpawn(world, player, true);
        }
        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(ModItems.RUSTIC_BOTTLE.get());
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(ModItems.RUSTIC_BOTTLE.get()));
            }
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.hexalia.homestead_brew").withStyle(ChatFormatting.DARK_BLUE));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }
}
