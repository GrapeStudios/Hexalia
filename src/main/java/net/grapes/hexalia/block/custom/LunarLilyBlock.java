package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.entity.LunarLilyBlockEntity;
import net.grapes.hexalia.block.entity.ModBlockEntities;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LunarLilyBlock extends EnchantedPlantBlock implements BlockEntityProvider {

    public LunarLilyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LunarLilyBlockEntity lunarLily && lunarLily.isActive()) {
                for (int i = 0; i < 6; i++) {
                    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    double y = pos.getY() + 0.7 + random.nextDouble() * 0.3;
                    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    world.addParticle(ParticleTypes.EFFECT, x, y, z, 0, 0.01, 0);
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!isNight(world)) {
            return ActionResult.FAIL;
        }

        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == ModItems.MOON_CRYSTAL) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LunarLilyBlockEntity lunarLily) {
                if (!lunarLily.isActive()) {
                    lunarLily.activate(world.getTime());
                    world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
                            SoundCategory.BLOCKS, 1.0f, 1.0f);
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    private static boolean isNight(World world) {
        long time = world.getTimeOfDay();
        return time > 13000 && time < 23000;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LunarLilyBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return type == ModBlockEntities.LUNAR_LILY_BE ? (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof LunarLilyBlockEntity lunarLily) {
                LunarLilyBlockEntity.tick(world1, pos, state1, lunarLily);
            }
        } : null;
    }
}
