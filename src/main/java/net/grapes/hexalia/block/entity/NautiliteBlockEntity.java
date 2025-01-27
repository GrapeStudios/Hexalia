package net.grapes.hexalia.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NautiliteBlockEntity extends BlockEntity {
    private int activeTicks = 0;
    private static final int DURATION = 2400;
    private static final int EFFECT_RADIUS = 16;

    public NautiliteBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.NAUTILITE_BE.get(), pPos, pBlockState);
    }

    public void activate() {
        this.activeTicks = DURATION;
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, NautiliteBlockEntity pBlockEntity) {
        if (pBlockEntity.isActive()) {
            pBlockEntity.activeTicks--;

            if (pLevel instanceof ServerLevel serverLevel) {
                AABB area = new AABB(pPos).inflate(EFFECT_RADIUS);
                List<Player> players = serverLevel.getEntitiesOfClass(Player.class, area);
                List<LivingEntity> mobs = serverLevel.getEntitiesOfClass(LivingEntity.class, area);

                for (Player player : players) {
                    if (player.isInWaterOrRain()) {
                        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 40, 0, true, false));
                    }
                }

                for (LivingEntity mob : mobs) {
                    if ((mob instanceof Drowned || mob instanceof Guardian) && mob.isInWaterOrRain() && pPos.closerThan(mob.blockPosition(), EFFECT_RADIUS)) {
                        mob.hurt(pLevel.damageSources().magic(), 4.0F);
                        serverLevel.sendParticles(ParticleTypes.BUBBLE, mob.getX(), mob.getY(), mob.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }

                emitParticles(serverLevel, pPos);
            }
            if (pBlockEntity.activeTicks <= 0) {
                pLevel.playSound(null, pPos, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.destroyBlock(pPos, false);
            }
        }
    }

    private static void emitParticles(ServerLevel pLevel, BlockPos pPos) {
        Vec3 center = new Vec3(pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);
        RandomSource random = pLevel.random;
        for (int i = 0; i < 5; i++) {
            double x = center.x + (random.nextDouble() - 0.5) * 2;
            double y = center.y + (random.nextDouble() - 0.5) * 2;
            double z = center.z + (random.nextDouble() - 0.5) * 2;
            pLevel.sendParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}