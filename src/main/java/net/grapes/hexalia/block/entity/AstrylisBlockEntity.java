package net.grapes.hexalia.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlockEntity extends BlockEntity {

    private static final int DEFAULT_DURATION = 1200;
    private static final int BONEMEAL_INTERVAL = 240;
    private long activationTime = -1;
    private int duration = DEFAULT_DURATION;
    private long lastBonemealTime = -1;

    public AstrylisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASTRYLIS_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstrylisBlockEntity entity) {
        if (world instanceof ServerWorld serverWorld && entity.isActive()) {
            long currentTime = world.getTime();
            long elapsedTime = currentTime - entity.activationTime;

            if (elapsedTime >= entity.duration) {
                entity.deactivate();
                return;
            }

            long expectedBonemealApplications = elapsedTime / BONEMEAL_INTERVAL;
            long actualBonemealApplications = entity.lastBonemealTime == -1 ? 0 :
                    (entity.lastBonemealTime - entity.activationTime) / BONEMEAL_INTERVAL + 1;

            if (expectedBonemealApplications > actualBonemealApplications) {
                long missedApplications = Math.min(expectedBonemealApplications - actualBonemealApplications, 5);
                for (long i = 0; i < missedApplications; i++) {
                    applyBonemealToCropsAndSaplings(serverWorld, pos);
                }
                entity.lastBonemealTime = currentTime;
            } else if (elapsedTime % BONEMEAL_INTERVAL == 0 && elapsedTime > 0) {
                applyBonemealToCropsAndSaplings(serverWorld, pos);
                entity.lastBonemealTime = currentTime;
            }

            entity.markDirty();
        }
    }

    private static void applyBonemealToCropsAndSaplings(ServerWorld world, BlockPos centerPos) {
        BlockPos.iterate(centerPos.add(-4, -2, -4), centerPos.add(4, 2, 4)).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof Fertilizable fertilizable &&
                    (state.isIn(BlockTags.CROPS) || state.isIn(BlockTags.SAPLINGS))) {
                if (fertilizable.isFertilizable(world, pos, state, false)) {
                    fertilizable.grow(world, world.getRandom(), pos, state);
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0);
                }
            }
        });
    }

    public boolean isActive() {
        return activationTime > 0 && world != null && world.getTime() >= activationTime;
    }

    public void activate(long gameTime) {
        activate(gameTime, DEFAULT_DURATION);
    }

    public void activate(long gameTime, int customDuration) {
        this.activationTime = gameTime;
        this.duration = customDuration;
        this.lastBonemealTime = -1;
        this.markDirty();
    }

    public void deactivate() {
        this.activationTime = -1;
        this.lastBonemealTime = -1;
        this.markDirty();
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        if (!isActive() || world == null) {
            return 0.0f;
        }
        long elapsed = world.getTime() - activationTime;
        return Math.min(1.0f, (float) elapsed / duration);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("activationTime", activationTime);
        nbt.putInt("duration", duration);
        nbt.putLong("lastBonemealTime", lastBonemealTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activationTime = nbt.getLong("activationTime");
        this.duration = nbt.contains("duration") ? nbt.getInt("duration") : DEFAULT_DURATION;
        this.lastBonemealTime = nbt.getLong("lastBonemealTime");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.putLong("activationTime", activationTime);
        nbt.putInt("duration", duration);
        nbt.putLong("lastBonemealTime", lastBonemealTime);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}