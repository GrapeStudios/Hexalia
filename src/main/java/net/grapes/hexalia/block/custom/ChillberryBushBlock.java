package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ChillberryBushBlock extends SweetBerryBushBlock {
    public ChillberryBushBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.CHILLBERRY);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.FOX || entity.getType() == EntityType.BEE) {
            return;
        }
        entity.slowMovement(state, new Vec3d(0.8f, 0.75, 0.8f));
        if (!(world.isClient || state.get(AGE) <= 0 || entity.lastRenderX == entity.getX() && entity.lastRenderZ == entity.getZ())) {
            double d = Math.abs(entity.getX() - entity.lastRenderX);
            double e = Math.abs(entity.getZ() - entity.lastRenderZ);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        if (i != 3) {
            return super.onUse(state, world, pos, player, hand, hit);
        }
        if (player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        }
        int j = 1 + world.random.nextInt(2);
        ChillberryBushBlock.dropStack(world, pos, new ItemStack(ModItems.CHILLBERRY, j + 1));
        world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
        BlockState blockState = (BlockState)state.with(AGE, 1);
        world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
        return ActionResult.success(world.isClient);
    }
}
