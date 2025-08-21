package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class GrimshadeBlock extends EnchantedPlantBlock {
    public GrimshadeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.NETHERRACK)
                || floor.isOf(Blocks.SOUL_SAND) || floor.isOf(Blocks.SOUL_SOIL);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient){
            for (int i = 0; i < 3; i++) {
                    double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
                    double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
                    double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
                    world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        LivingEntity livingEntity;
        if (world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (entity instanceof LivingEntity && !(livingEntity = (LivingEntity)entity).isInvulnerableTo(world.getDamageSources().wither())) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem() == ModItems.HEX_FOCUS) {
            if (!world.isClient) {
                spawnActivationEffects(world, pos);
                transformSkeletons((ServerWorld) world, pos);
                transformSkulls((ServerWorld) world, pos);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void spawnActivationEffects(World world, BlockPos pos) {
        spawnParticles((ServerWorld) world, pos);
        playDecayingSounds(world, pos);
    }

    private void playDecayingSounds(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(null, pos, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        Box area = new Box(pos).expand(2.5);
        for (int i = 0; i < 50; i++) {
            double x = area.minX + world.random.nextDouble() * (area.maxX - area.minX);
            double y = area.minY + world.random.nextDouble() * (area.maxY - area.minY);
            double z = area.minZ + world.random.nextDouble() * (area.maxZ - area.minZ);
            world.spawnParticles(ParticleTypes.SMOKE, x, y, z, 2, 0, 0, 0, 0);
        }
    }

    private void transformSkeletons(ServerWorld world, BlockPos pos) {
        Box area = new Box(pos).expand(2.5);
        boolean skeletonTransformed = false;

        for (Entity entity : world.getEntitiesByClass(SkeletonEntity.class, area, e -> true)) {
            WitherSkeletonEntity witherSkeleton = EntityType.WITHER_SKELETON.create(world);
            if (witherSkeleton != null) {
                witherSkeleton.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                entity.discard();
                world.spawnEntity(witherSkeleton);
                skeletonTransformed = true;
            }
        }

        if (skeletonTransformed) {
            Random random = world.getRandom();
            double chance = random.nextDouble();

            if (chance < 0.75) {
                spawnWitherRose(world, pos, random);

                if (chance < 0.50) {
                    spawnWitherRose(world, pos, random);
                }
            }

            world.breakBlock(pos, false);
        }
    }

    private void transformSkulls(ServerWorld world, BlockPos pos) {
        Box area = new Box(pos).expand(2.5);
        final int[] skullsTransformed = {0};

        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, area, entity ->
                entity.getStack().getItem() == Items.SKELETON_SKULL && skullsTransformed[0] < 3)) {
            itemEntity.setStack(new ItemStack(Items.WITHER_SKELETON_SKULL, itemEntity.getStack().getCount()));
            skullsTransformed[0]++;
        }

        BlockPos.iterate(
                (int) area.minX, (int) area.minY, (int) area.minZ,
                (int) area.maxX, (int) area.maxY, (int) area.maxZ
        ).forEach(blockPos -> {
            BlockState blockState = world.getBlockState(blockPos);
            if (skullsTransformed[0] < 3) {
                if (blockState.isOf(Blocks.SKELETON_SKULL)) {
                    world.setBlockState(blockPos, Blocks.WITHER_SKELETON_SKULL.getDefaultState()
                            .with(SkullBlock.ROTATION, blockState.get(SkullBlock.ROTATION)), 3);
                    skullsTransformed[0]++;
                } else if (blockState.isOf(Blocks.SKELETON_WALL_SKULL)) {
                    world.setBlockState(blockPos, Blocks.WITHER_SKELETON_WALL_SKULL.getDefaultState()
                            .with(SkullBlock.ROTATION, blockState.get(SkullBlock.ROTATION)), 3);
                    skullsTransformed[0]++;
                } else if (blockState.isOf(ModBlocks.CANDLE_SKULL)) {
                    world.setBlockState(blockPos, ModBlocks.WITHER_CANDLE_SKULL.getDefaultState()
                            .with(SkullBlock.ROTATION, blockState.get(SkullBlock.ROTATION)), 3);
                    skullsTransformed[0]++;
                }

            }
        });

        if (skullsTransformed[0] > 0) {
            world.breakBlock(pos, false);
        }
    }


    private void spawnWitherRose(ServerWorld world, BlockPos pos, Random random) {
        int x = pos.getX() + random.nextInt(5) - 2;
        int z = pos.getZ() + random.nextInt(5) - 2;
        BlockPos rosePos = new BlockPos(x, pos.getY(), z);

        if (world.isAir(rosePos) && world.getBlockState(rosePos.down()).isSolidBlock(world, rosePos.down())) {
            world.setBlockState(rosePos, Blocks.WITHER_ROSE.getDefaultState(), 3);
        }
    }
}


