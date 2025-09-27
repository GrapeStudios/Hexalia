package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class HexFocusItem extends Item {

    private static final int BLOCK_BREAK_EVENT_ID = 2001;
    private static final Set<Block> VALID_BLOCKS = Set.of(Blocks.COBBLED_DEEPSLATE, Blocks.DEEPSLATE);

    public HexFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (player != null) {
            if (!world.isClient) {
                if (player.getItemCooldownManager().isCoolingDown(this)) {
                    return ActionResult.FAIL;
                }
            }

            if (VALID_BLOCKS.contains(block)) {
                world.syncWorldEvent(BLOCK_BREAK_EVENT_ID, pos, Block.getRawIdFromState(state));
                world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.setBlockState(pos, ModBlocks.RITUAL_TABLE.getDefaultState());
                player.getItemCooldownManager().set(this, 60);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
