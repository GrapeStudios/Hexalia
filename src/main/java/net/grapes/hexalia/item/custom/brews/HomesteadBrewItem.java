package net.grapes.hexalia.item.custom.brews;

import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.util.TeleportUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class HomesteadBrewItem extends Item {
    public HomesteadBrewItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player,
                                            Hand hand) {
        if (!world.isClient && TeleportUtil.canReturn(world, player, true)) {
            return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
        }
        player.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entity) {
        PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        if (player==null||!player.isCreative()) {
            stack.decrement(1);
        }

        if (player!=null) {
            TeleportUtil.teleportPlayerToSpawn(worldIn, player, true);
        }

        if (player==null||!player.isCreative()) {
            if (stack.isEmpty()) {
                return new ItemStack(ModItems.RUSTIC_BOTTLE);
            }

            if (player!=null) {
                player.getInventory().insertStack(new ItemStack(ModItems.RUSTIC_BOTTLE));
            }
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
}
