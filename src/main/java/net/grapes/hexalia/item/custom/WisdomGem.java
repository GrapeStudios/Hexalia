package net.grapes.hexalia.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WisdomGem extends Item {
    public WisdomGem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (hand == Hand.OFF_HAND) {
            player.equipStack(EquipmentSlot.OFFHAND, stack);
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.fail(stack);
    }
}
