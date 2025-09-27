package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class InfusedDirtBlock extends Block {

    public InfusedDirtBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof HoeItem && state.getBlock() == ModBlocks.INFUSED_DIRT &&
                hit.getSide() != Direction.DOWN && world.getBlockState(pos.up()).isAir()) {
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient) {
                world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(world.getBlockState(pos),
                        ModBlocks.INFUSED_FARMLAND.getDefaultState(), world, pos));
                if (!player.isCreative()) {
                    stack.damage(1, player, LivingEntity.getSlotForHand(hand));
                }
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        spawnBubblesParticles(world, pos);
    }

    private void spawnBubblesParticles(World world, BlockPos pos) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < 8; i++) {
            double x = pos.getX() + 0.5 + rng.nextDouble(-0.5, 0.5);
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + rng.nextDouble(-0.5, 0.5);
            world.addParticle(ModParticleType.INFUSED_BUBBLE, x, y, z, 0.0, 0.05, 0.0);
        }
    }
}
