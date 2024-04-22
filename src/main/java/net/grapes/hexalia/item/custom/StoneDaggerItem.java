package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneDaggerItem extends Item {
    public StoneDaggerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity playerEntity = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (world.getBlockState(pos).getBlock() == Blocks.DARK_OAK_LOG) {
            world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, Blocks.STRIPPED_DARK_OAK_LOG.getDefaultState().with(PillarBlock.AXIS,
                    state.get(PillarBlock.AXIS)));
            if (playerEntity != null) {
                playerEntity.giveItemStack(new ItemStack(ModItems.RESIN));
            }
            if (playerEntity != null) {
                ItemStack stack = context.getStack();
                if (!playerEntity.isCreative() && stack.isDamageable()) {
                    stack.damage(1, playerEntity, (p) -> playerEntity.sendToolBreakStatus(context.getHand()));
                    if (stack.isEmpty()) {
                        playerEntity.setStackInHand(context.getHand(), ItemStack.EMPTY);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
}
