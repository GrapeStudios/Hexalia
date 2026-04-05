package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NautiliteBlockEntity extends BlockEntity {
    private int activeTicks = 0;
    private long activationTime = -1;

    public NautiliteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.NAUTILITE, pos, state);
    }

    private static int duration() {
        return Math.max(1, Configuration.NAUTILITE_DURATION.get());
    }

    private static int effectRadius() {
        return Math.max(1, Configuration.NAUTILITE_EFFECT_RADIUS.get());
    }

    public void activate() {
        activeTicks = duration();
        if (world != null) {
            activationTime = world.getTime();
        }
        markDirty();
        sync();
    }

    public boolean isActive() {
        return activeTicks > 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, NautiliteBlockEntity blockEntity) {
        if (blockEntity.activationTime != -1) {
            long now = world.getTime();
            long elapsed = now - blockEntity.activationTime;
            int expected = duration() - (int) elapsed;
            if (Math.abs(blockEntity.activeTicks - expected) > 5) {
                blockEntity.activeTicks = Math.max(0, expected);
            }
        }

        if (!blockEntity.isActive()) {
            return;
        }

        blockEntity.activeTicks--;

        if (world instanceof ServerWorld serverWorld) {
            int radius = effectRadius();
            Box area = new Box(pos).expand(radius);

            List<PlayerEntity> players = serverWorld.getEntitiesByClass(PlayerEntity.class, area, player -> true);
            for (PlayerEntity player : players) {
                if (player.isTouchingWaterOrRain()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 40, 0, true, false));
                }
                if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                    player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                }
            }

            List<LivingEntity> mobs = serverWorld.getEntitiesByClass(LivingEntity.class, area, entity -> true);
            for (LivingEntity mob : mobs) {
                if ((mob instanceof DrownedEntity || mob instanceof GuardianEntity)
                        && mob.isTouchingWaterOrRain()
                        && pos.isWithinDistance(mob.getBlockPos(), radius)) {
                    mob.damage(world.getDamageSources().magic(), 2.0F);
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE, mob.getX(), mob.getY(), mob.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }

            emitParticles(serverWorld, pos);
        }

        if (blockEntity.activeTicks <= 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.removeBlock(pos, false);
        } else {
            blockEntity.markDirty();
            blockEntity.sync();
        }
    }

    private static void emitParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Random random = world.getRandom();

        for (int i = 0; i < 5; i++) {
            double x = center.x + (random.nextDouble() - 0.5) * 2.0;
            double y = center.y + (random.nextDouble() - 0.5) * 2.0;
            double z = center.z + (random.nextDouble() - 0.5) * 2.0;
            world.spawnParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        activeTicks = nbt.getInt("ActiveTicks");
        activationTime = nbt.getLong("ActivationTime");

        if (activationTime != -1 && world != null && activeTicks > 0) {
            long now = world.getTime();
            long elapsed = now - activationTime;
            activeTicks = Math.max(0, duration() - (int) elapsed);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}