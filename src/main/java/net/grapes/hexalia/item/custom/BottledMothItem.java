package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BottledMothItem extends Item {
    public BottledMothItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();

        if (!world.isClientSide && player != null) {
            BlockPos spawnPos = blockPos.relative(direction);
            SilkMothEntity silkMothEntity = new SilkMothEntity(ModEntities.SILK_MOTH_ENTITY.get(), world);

            CompoundTag nbt = itemStack.getTag();
            if (nbt != null) {
                silkMothEntity.load(nbt);
            }

            silkMothEntity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            world.addFreshEntity(silkMothEntity);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);

            itemStack.shrink(1);
            player.getInventory().add(new ItemStack(ModItems.RUSTIC_BOTTLE.get()));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
