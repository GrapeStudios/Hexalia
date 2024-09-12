package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.Block;
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
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == Blocks.DARK_OAK_LOG || state.getBlock() == ModBlocks.COTTONWOOD_LOG) {
            world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            Block strippedBlock = (state.getBlock() == Blocks.DARK_OAK_LOG) ? Blocks.STRIPPED_DARK_OAK_LOG : ModBlocks.STRIPPED_COTTONWOOD_LOG;
            world.setBlockState(pos, strippedBlock.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)));
            if (player != null) {
                player.giveItemStack(new ItemStack(ModItems.RESIN));
                ItemStack stack = context.getStack();
                if (!player.isCreative() && stack.isDamageable()) {
                    stack.damage(1, player, (p) -> p.sendToolBreakStatus(context.getHand()));
                    if (stack.isEmpty()) {
                        player.setStackInHand(context.getHand(), ItemStack.EMPTY);
                    }
                }
            }
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

}
