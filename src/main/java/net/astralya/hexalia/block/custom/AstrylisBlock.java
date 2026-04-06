package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.entity.custom.AstrylisBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
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
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlock extends EnchantedPlantBlock implements BlockEntityProvider {

    public AstrylisBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AstrylisBlockEntity astrylis && astrylis.isActive()) {
                float progress = astrylis.getProgress();
                int particleCount = Math.max(2, (int) (6 * (1.0f - progress * 0.5f)));
                for (int i = 0; i < particleCount; i++) {
                    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    double y = pos.getY() + 0.7 + random.nextDouble() * 0.3;
                    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    world.addParticle(ModParticleType.SPARKLE, x, y, z, 0, 0.01, 0);
                }
            }
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AstrylisBlockEntity astrylis) {
            if (itemStack.isEmpty()) {
                if (!astrylis.isActive()) {
                    player.sendMessage(
                            Text.translatable("message.hexalia.astrylis.inactive"),
                            true
                    );
                }
                return ItemActionResult.SUCCESS;
            }
            if (itemStack.isOf(ModItems.CELESTIAL_CRYSTAL)) {
                if (!astrylis.isActive()) {
                    astrylis.activate(world.getTime());
                    world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                    }
                    player.sendMessage(
                            Text.translatable("message.hexalia.astrylis.activation"),
                            true
                    );
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AstrylisBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof AstrylisBlockEntity astrylis) {
                AstrylisBlockEntity.tick(world1, pos, state1, astrylis);
            }
        };
    }
}