package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.worlddata.fireproof.FireproofWaxRuntime;
import net.astralya.hexalia.worlddata.fireproof.WaxedBlocksSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class FireproofWaxItem extends Item {
    public FireproofWaxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!state.isFlammable(level, pos, context.getClickedFace())) {
            return InteractionResult.PASS;
        }

        WaxedBlocksSavedData data = WaxedBlocksSavedData.get(level);
        if (data.isWaxed(pos)) {
            return InteractionResult.CONSUME;
        }

        data.wax(pos, state);
        FireproofWaxRuntime.get(level).onWaxAdded(level, pos);

        ItemStack stack = context.getItemInHand();
        if (player == null || !player.isCreative()) {
            stack.shrink(1);
        }

        level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }
}