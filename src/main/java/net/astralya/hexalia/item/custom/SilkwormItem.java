package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.SilkwormCocoonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SilkwormItem extends Item {

    public SilkwormItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos clickedPos = context.getBlockPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        if (!clickedState.isIn(BlockTags.LOGS)) {
            return ActionResult.PASS;
        }

        Direction face = context.getSide();
        if (face.getAxis().isVertical()) {
            return ActionResult.FAIL;
        }

        BlockPos placePos = clickedPos.offset(face);
        BlockState placeState = world.getBlockState(placePos);

        if (!placeState.isAir() && !placeState.isReplaceable()) {
            return ActionResult.FAIL;
        }

        BlockState cocoonState = ModBlocks.SILKWORM_COCOON
                .getDefaultState()
                .with(SilkwormCocoonBlock.FACING, face);

        if (!cocoonState.canPlaceAt(world, placePos)) {
            return ActionResult.FAIL;
        }

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        world.setBlockState(placePos, cocoonState, 3);
        world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, placePos);
        world.playSound(null, placePos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.8F, 1.0F);

        PlayerEntity player = context.getPlayer();
        if (player == null || !player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }

        return ActionResult.CONSUME;
    }
}