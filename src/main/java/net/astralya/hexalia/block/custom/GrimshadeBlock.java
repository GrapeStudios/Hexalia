package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;

public class GrimshadeBlock extends EnchantedPlantBlock {

    public GrimshadeBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() == ModItems.HEX_FOCUS.get()) {
            if (!pLevel.isClientSide) {
                spawnActivationEffects(pLevel, pPos);
                transformSkulls((ServerLevel) pLevel, pPos);
                transformSkeletons((ServerLevel) pLevel, pPos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide) {
            for (int i = 0; i < 3; i++) {
                double x = pPos.getX() + 0.5 + (pRandom.nextDouble() - 0.5) * 0.5;
                double y = pPos.getY() + 0.5 + (pRandom.nextDouble() - 0.5) * 0.5;
                double z = pPos.getZ() + 0.5 + (pRandom.nextDouble() - 0.5) * 0.5;
                pLevel.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return super.mayPlaceOn(pState, pLevel, pPos) || pState.is(Blocks.NETHERRACK) || pState.is(Blocks.SOUL_SAND) || pState.is(Blocks.SOUL_SOIL);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide && pLevel.getDifficulty() != Difficulty.PEACEFUL) {
            if (pEntity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)pEntity;
                if (!livingentity.isInvulnerableTo(pLevel.damageSources().wither())) {
                    livingentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
                }
            }

        }
    }

    private void spawnActivationEffects(Level pLevel, BlockPos pPos) {
        spawnParticles((ServerLevel) pLevel, pPos);
        playDecayingSounds(pLevel, pPos);
    }

    private void spawnParticles(ServerLevel level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(2.5);
        for (int i = 0; i < 50; i++) {
            double x = area.minX + level.random.nextDouble() * (area.maxX - area.minX);
            double y = area.minY + level.random.nextDouble() * (area.maxY - area.minY);
            double z = area.minZ + level.random.nextDouble() * (area.maxZ - area.minZ);
            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 2, 0, 0, 0, 0);
        }
    }

    private void playDecayingSounds(Level pLevel, BlockPos pos) {
        pLevel.playSound(null, pos, SoundEvents.WITHER_SKELETON_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
        pLevel.playSound(null, pos, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private void transformSkeletons(ServerLevel pLevel, BlockPos pPos) {
        AABB area = new AABB(pPos).inflate(2.5);
        boolean skeletonTransformed = false;
        for (Entity entity : pLevel.getEntities(null, area)) {
            if (entity instanceof Skeleton skeleton) {
                WitherSkeleton witherSkeleton = EntityType.WITHER_SKELETON.create(pLevel);
                if (witherSkeleton != null) {
                    witherSkeleton.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
                    skeleton.discard();
                    pLevel.addFreshEntity(witherSkeleton);
                    skeletonTransformed = true;
                }
            }
        }

        if (skeletonTransformed) {
            RandomSource random = pLevel.getRandom();
            double chance = random.nextDouble();

            if (chance < 0.75) {
                spawnWitherRose(pLevel, pPos, random);

                if (chance < 0.50) {
                    spawnWitherRose(pLevel, pPos, random);
                }
            }

            pLevel.destroyBlock(pPos, false);
        }
    }

    private void transformSkulls(ServerLevel pLevel, BlockPos pPos) {
        AABB area = new AABB(pPos).inflate(2.5);
        int skullsTransformed = 0;

        for (Entity entity : pLevel.getEntities(null, area)) {
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity itemEntity) {
                if (itemEntity.getItem().getItem() == Items.SKELETON_SKULL && skullsTransformed < 3) {
                    itemEntity.setItem(new net.minecraft.world.item.ItemStack(Items.WITHER_SKELETON_SKULL, itemEntity.getItem().getCount()));
                    skullsTransformed++;
                }
            }
        }

        for (BlockPos blockPos : BlockPos.betweenClosed(
                (int) area.minX, (int) area.minY, (int) area.minZ,
                (int) area.maxX, (int) area.maxY, (int) area.maxZ)) {
            BlockState blockState = pLevel.getBlockState(blockPos);
            if (blockState.is(Blocks.SKELETON_SKULL) && skullsTransformed < 3) {
                pLevel.setBlock(blockPos, Blocks.WITHER_SKELETON_SKULL.defaultBlockState(), 3);
                skullsTransformed++;
            } else if (blockState.is(Blocks.SKELETON_WALL_SKULL) && skullsTransformed < 3) {
                pLevel.setBlock(blockPos, Blocks.WITHER_SKELETON_WALL_SKULL.defaultBlockState()
                        .setValue(SkullBlock.ROTATION, blockState.getValue(SkullBlock.ROTATION)), 3);
                skullsTransformed++;
            } else if (blockState.is(ModBlocks.CANDLE_SKULL.get()) && skullsTransformed < 3) {
                pLevel.setBlock(blockPos, ModBlocks.WITHER_CANDLE_SKULL.get().defaultBlockState(), 3);
                skullsTransformed++;
            }
        }

        if (skullsTransformed > 0) {
            pLevel.destroyBlock(pPos, false);
        }
    }

    private void spawnWitherRose(ServerLevel pLevel, BlockPos pPos, RandomSource random) {
        int x = pPos.getX() + random.nextInt(5) - 2;
        int z = pPos.getZ() + random.nextInt(5) - 2;
        BlockPos rosePos = new BlockPos(x, pPos.getY(), z);
        if (pLevel.isEmptyBlock(rosePos) && pLevel.getBlockState(rosePos.below()).canSustainPlant(pLevel, rosePos.below(), net.minecraft.core.Direction.UP,
                (IPlantable) Blocks.WITHER_ROSE)) {
            pLevel.setBlock(rosePos, Blocks.WITHER_ROSE.defaultBlockState(), 3);
        }
    }
}