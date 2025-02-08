package net.grapes.hexalia.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class NautiliteBlockEntity extends BlockEntity {

    private int activeTicks = 0;
    private static final int DURATION = 2400;
    private static final int EFFECT_RADIUS = 16;

    public NautiliteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NAUTILITE_BE, pos, state);
    }

    public void activate() {
        this.activeTicks = DURATION;
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, NautiliteBlockEntity blockEntity) {
        if (blockEntity.isActive()) {
            blockEntity.activeTicks--;

            if (world instanceof ServerWorld serverWorld) {
                Box area = new Box(pos).expand(EFFECT_RADIUS);
                List<PlayerEntity> players = serverWorld.getEntitiesByClass(PlayerEntity.class, area, e -> true);
                List<LivingEntity> mobs = serverWorld.getEntitiesByClass(LivingEntity.class, area, e -> true);

                for (PlayerEntity player : players) {
                    if (player.isTouchingWaterOrRain()) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 40, 0, true, false));
                    }

                    if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                    }
                }

                for (LivingEntity mob : mobs) {
                    if ((mob instanceof DrownedEntity || mob instanceof GuardianEntity) && mob.isTouchingWaterOrRain() &&
                            pos.isWithinDistance(mob.getBlockPos(), EFFECT_RADIUS)) {
                        mob.damage(world.getDamageSources().magic(), 2.0F);
                        serverWorld.spawnParticles(ParticleTypes.BUBBLE, mob.getX(), mob.getY(),
                                mob.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                emitParticles(serverWorld, pos);
            }
            if (blockEntity.activeTicks <= 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.removeBlock(pos, false);
            }
        }
    }

    private static void emitParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Random random = world.getRandom();

        for (int i = 0; i < 5; i++) {
            double x = center.x + (random.nextDouble() - 0.5) * 2;
            double y = center.y + (random.nextDouble() - 0.5) * 2;
            double z = center.z + (random.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

}
