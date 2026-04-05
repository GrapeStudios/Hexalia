package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrimshadeBlockEntity extends BlockEntity {
    private int activeTicks = 0;
    private long activationTime = -1;

    public GrimshadeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GRIMSHADE, pos, state);
    }

    private static int duration() {
        int value = Configuration.GRIMSHADE_DURATION.get();
        return Math.max(1, value <= 0 ? 2400 : value);
    }

    private static int effectRadius() {
        int value = Configuration.GRIMSHADE_EFFECT_RADIUS.get();
        return Math.max(1, value <= 0 ? 8 : value);
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

    public static void tick(World world, BlockPos pos, BlockState state, GrimshadeBlockEntity blockEntity) {
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
            if (serverWorld.getDifficulty() != Difficulty.PEACEFUL) {
                int radius = effectRadius();
                Box area = new Box(pos).expand(radius);
                List<LivingEntity> targets = serverWorld.getEntitiesByClass(LivingEntity.class, area, entity -> entity.isAlive() && !(entity instanceof PlayerEntity));
                for (LivingEntity mob : targets) {
                    mob.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 0, true, true));
                    mob.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, true, true));
                }
            }

            emitParticles(serverWorld, pos);
        }

        if (blockEntity.activeTicks <= 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 0.6f, 0.7f);
        }

        blockEntity.markDirty();
        blockEntity.sync();
    }

    public void applyCollisionPing(LivingEntity entity) {
        if (world == null || world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        if (entity instanceof PlayerEntity) {
            return;
        }

        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 0, true, true));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, true, true));
    }

    private static void emitParticles(ServerWorld world, BlockPos pos) {
        if (world.getRandom().nextInt(3) != 0) {
            return;
        }

        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
        Random random = world.getRandom();
        int count = 6 + random.nextInt(4);

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.6;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 0.6;
            world.spawnParticles(ParticleTypes.SMOKE, center.x + offsetX, center.y + offsetY, center.z + offsetZ, 1, 0.0, 0.001, 0.0, 0.0);
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