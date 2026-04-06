package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.block.entity.custom.GrimshadeBlockEntity;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrimshadeBlock extends EnchantedPlantBlock implements BlockEntityProvider {

    public GrimshadeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem() != ModItems.HEX_FOCUS) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrimshadeBlockEntity grim && !grim.isActive()) {
                playActivationEffects((ServerWorld) world, pos);
                doOneShotConversions((ServerWorld) world, pos);
                grim.activate();
            }
        }
        return ItemActionResult.SUCCESS;
    }

    private void playActivationEffects(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.spawnParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 24, 0.35, 0.35, 0.35, 0.02);
        world.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 16, 0.35, 0.35, 0.35, 0.01);
    }

    private void doOneShotConversions(ServerWorld world, BlockPos pos) {
        Box area = new Box(pos).expand(2.5);

        for (Entity e : world.getEntitiesByClass(SkeletonEntity.class, area, x -> true)) {
            WitherSkeletonEntity wither = EntityType.WITHER_SKELETON.create(world);
            if (wither != null) {
                wither.refreshPositionAndAngles(e.getX(), e.getY(), e.getZ(), e.getYaw(), e.getPitch());
                e.discard();
                world.spawnEntity(wither);
            }
        }

        final int[] skulls = {0};
        for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class, area, it -> it.getStack().isOf(net.minecraft.item.Items.SKELETON_SKULL) && skulls[0] < 3)) {
            item.setStack(new net.minecraft.item.ItemStack(net.minecraft.item.Items.WITHER_SKELETON_SKULL, item.getStack().getCount()));
            skulls[0]++;
        }

        BlockPos.iterate((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX, (int) area.maxY, (int) area.maxZ).forEach(p -> {
            if (skulls[0] >= 3) return;
            BlockState s = world.getBlockState(p);
            if (s.isOf(Blocks.SKELETON_SKULL)) {
                world.setBlockState(p, Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, s.get(SkullBlock.ROTATION)), 3);
                skulls[0]++;
            } else if (s.isOf(Blocks.SKELETON_WALL_SKULL)) {
                world.setBlockState(p, Blocks.WITHER_SKELETON_WALL_SKULL.getDefaultState().with(SkullBlock.ROTATION, s.get(SkullBlock.ROTATION)), 3);
                skulls[0]++;
            } else if (s.isOf(ModBlocks.CANDLE_SKULL)) {
                world.setBlockState(p, ModBlocks.WITHER_CANDLE_SKULL.getDefaultState().with(SkullBlock.ROTATION, s.get(SkullBlock.ROTATION)), 3);
                skulls[0]++;
            }
        });
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos)
                || floor.isOf(Blocks.NETHERRACK)
                || floor.isOf(Blocks.SOUL_SAND)
                || floor.isOf(Blocks.SOUL_SOIL);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrimshadeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return type == ModBlockEntityTypes.GRIMSHADE ? (w, p, s, be) -> {
            if (be instanceof GrimshadeBlockEntity grim) {
                GrimshadeBlockEntity.tick(w, p, s, grim);
            }
        } : null;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, net.minecraft.entity.Entity entity) {
        if (world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) return;
        if (!(entity instanceof net.minecraft.entity.LivingEntity) || entity instanceof PlayerEntity) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof GrimshadeBlockEntity grim && grim.isActive()) {
            grim.applyCollisionPing((net.minecraft.entity.LivingEntity) entity);
        }
    }
}