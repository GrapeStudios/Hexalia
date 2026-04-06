package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlockEntity extends BlockEntity {

    private long activationTime = -1;
    private long lastBonemealTime = -1;

    public AstrylisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ASTRYLIS, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstrylisBlockEntity entity) {
        if (world instanceof ServerWorld serverWorld && entity.isActive()) {
            long currentTime = world.getTime();
            long elapsedTime = currentTime - entity.activationTime;
            int interval = Configuration.ASTRYLIS_BONEMEAL_INTERVAL.get();
            int duration = Configuration.ASTRYLIS_DURATION.get();
            if (elapsedTime >= duration) {
                entity.deactivate();
                return;
            }
            long expectedApplications = elapsedTime / interval;
            long actualApplications = entity.lastBonemealTime == -1 ? 0 :
                    (entity.lastBonemealTime - entity.activationTime) / interval + 1;
            if (expectedApplications > actualApplications) {
                long missed = Math.min(expectedApplications - actualApplications, 5);
                for (long i = 0; i < missed; i++) {
                    applyBonemealToCropsAndSaplings(serverWorld, pos);
                }
                entity.lastBonemealTime = currentTime;
            } else if (elapsedTime % interval == 0 && elapsedTime > 0) {
                applyBonemealToCropsAndSaplings(serverWorld, pos);
                entity.lastBonemealTime = currentTime;
            }
            entity.markDirty();
        }
    }

    private static void applyBonemealToCropsAndSaplings(ServerWorld world, BlockPos centerPos) {
        BlockPos.stream(centerPos.add(-4, -2, -4), centerPos.add(4, 2, 4)).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof Fertilizable fertilizable &&
                    (state.isIn(BlockTags.CROPS) || state.isIn(BlockTags.SAPLINGS))) {
                if (fertilizable.isFertilizable(world, pos, state)) {
                    fertilizable.grow(world, world.random, pos, state);
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0);
                }
            }
        });
    }

    public boolean isActive() {
        return activationTime > 0 && world != null && world.getTime() >= activationTime;
    }

    public void activate(long gameTime) {
        this.activationTime = gameTime;
        this.lastBonemealTime = -1;
        this.markDirty();
        sync();
    }

    public void deactivate() {
        this.activationTime = -1;
        this.lastBonemealTime = -1;
        this.markDirty();
        sync();
    }

    private void sync() {
        if (this.world == null || this.world.isClient) return;
        BlockState state = this.getCachedState();
        this.world.updateListeners(this.pos, state, state, 3);
    }

    public int getDuration() {
        return Configuration.ASTRYLIS_DURATION.get();
    }

    public float getProgress() {
        if (!isActive() || world == null) return 0.0f;
        long elapsed = world.getTime() - activationTime;
        return Math.min(1.0f, (float) elapsed / getDuration());
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putLong("activationTime", activationTime);
        nbt.putLong("lastBonemealTime", lastBonemealTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.activationTime = nbt.getLong("activationTime");
        this.lastBonemealTime = nbt.getLong("lastBonemealTime");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putLong("activationTime", activationTime);
        nbt.putLong("lastBonemealTime", lastBonemealTime);
        return nbt;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}