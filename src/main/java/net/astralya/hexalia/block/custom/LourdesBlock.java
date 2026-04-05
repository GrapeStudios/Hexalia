package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.LourdesBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LourdesBlock extends EnchantedPlantBlock implements BlockEntityProvider {
    public LourdesBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LourdesBlockEntity lourdes && lourdes.isActive()) {
            lourdes.spawnActiveParticles(world, pos, random);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof LourdesBlockEntity lourdes)) {
            return ActionResult.PASS;
        }

        if (held.isEmpty()) {
            if (!lourdes.isActive() && !world.isClient) {
                player.sendMessage(Text.translatable("message.hexalia.lourdes.inactive"), true);
            }
            return ActionResult.success(world.isClient);
        }

        if (!held.isOf(ModItems.LOTUS_BLOSSOM)) {
            return ActionResult.PASS;
        }

        if (lourdes.isActive()) {
            return ActionResult.success(world.isClient);
        }

        if (!world.isClient) {
            lourdes.activate(world.getTime());
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative()) {
                held.decrement(1);
            }
            player.sendMessage(Text.translatable("message.hexalia.lourdes.activation"), true);
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LourdesBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        return (tickWorld, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof LourdesBlockEntity lourdes) {
                LourdesBlockEntity.tick(tickWorld, blockPos, blockState, lourdes);
            }
        };
    }
}