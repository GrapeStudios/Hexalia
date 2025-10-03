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
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlockEntity extends BlockEntity {

    private long activationTime   = -1;
    private int  duration         = 0;
    private long lastBonemealTime = -1;

    public AstrylisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ASTRYLIS, pos, state);
    }

    private static int cfgDuration() {
        return Math.max(1, Configuration.common().plants.astrylisDuration);
    }
    private static int cfgInterval() {
        return Math.max(1, Configuration.common().plants.astrylisBonemealInterval);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstrylisBlockEntity be) {
        if (!(world instanceof ServerWorld server) || !be.isActive()) return;

        final long now       = world.getTime();
        final long elapsed   = now - be.activationTime;
        final int interval   = cfgInterval();

        if (elapsed >= be.duration) {
            be.deactivate();
            return;
        }

        long expectedApplications = elapsed / interval;
        long actualApplications   = (be.lastBonemealTime == -1)
                ? 0
                : ((be.lastBonemealTime - be.activationTime) / interval) + 1;

        if (expectedApplications > actualApplications) {
            long missed = Math.min(expectedApplications - actualApplications, 5);
            for (long i = 0; i < missed; i++) {
                applyBonemealToCropsAndSaplings(server, pos);
            }
            be.lastBonemealTime = now;
        } else if (elapsed % interval == 0 && elapsed > 0) {
            applyBonemealToCropsAndSaplings(server, pos);
            be.lastBonemealTime = now;
        }

        be.markDirty();
    }

    private static void applyBonemealToCropsAndSaplings(ServerWorld world, BlockPos center) {
        BlockPos.iterate(center.add(-4, -2, -4), center.add(4, 2, 4)).forEach(p -> {
            BlockState s = world.getBlockState(p);
            if (s.getBlock() instanceof Fertilizable fert
                    && (s.isIn(BlockTags.CROPS) || s.isIn(BlockTags.SAPLINGS))) {
                if (fert.isFertilizable(world, p, s, false)) {
                    fert.grow(world, world.getRandom(), p, s);
                    world.spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0
                    );
                }
            }
        });
    }

    public boolean isActive() {
        return activationTime > 0 && world != null && world.getTime() >= activationTime;
    }

    public void activate(long gameTime) {
        activate(gameTime, cfgDuration());
    }

    public void activate(long gameTime, int customDuration) {
        this.activationTime   = gameTime;
        this.duration         = Math.max(1, customDuration);
        this.lastBonemealTime = -1;
        this.markDirty();
    }

    public void deactivate() {
        this.activationTime   = -1;
        this.lastBonemealTime = -1;
        this.markDirty();
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        if (!isActive() || world == null || duration <= 0) return 0.0f;
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
        this.activationTime   = nbt.getLong("activationTime");
        this.duration         = nbt.contains("duration") ? nbt.getInt("duration") : cfgDuration();
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