package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AstrylisBlockEntity extends BlockEntity {

    private long activationTime = -1;
    private int duration = Configuration.ASTRYLIS_DURATION.get();
    private long lastBonemealTime = -1;

    public AstrylisBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypes.ASTRYLIS_BE.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AstrylisBlockEntity entity) {
        if (level instanceof ServerLevel serverLevel && entity.isActive()) {
            int interval = Math.max(1, Configuration.ASTRYLIS_BONEMEAL_INTERVAL.get());

            long currentTime = level.getGameTime();
            long elapsedTime = currentTime - entity.activationTime;

            if (elapsedTime >= entity.duration) {
                entity.deactivate();
                return;
            }

            long expectedBonemealApplications = elapsedTime / interval;
            long actualBonemealApplications = entity.lastBonemealTime == -1 ? 0
                    : (entity.lastBonemealTime - entity.activationTime) / interval + 1;

            if (expectedBonemealApplications > actualBonemealApplications) {
                long missedApplications = Math.min(expectedBonemealApplications - actualBonemealApplications, 5);
                for (long i = 0; i < missedApplications; i++) {
                    applyBonemealToCropsAndSaplings(serverLevel, pos);
                }
                entity.lastBonemealTime = currentTime;
            } else if (elapsedTime % interval == 0 && elapsedTime > 0) {
                applyBonemealToCropsAndSaplings(serverLevel, pos);
                entity.lastBonemealTime = currentTime;
            }

            entity.setChanged();
        }
    }

    private static void applyBonemealToCropsAndSaplings(ServerLevel level, BlockPos centerPos) {
        BlockPos.betweenClosedStream(centerPos.offset(-4, -2, -4), centerPos.offset(4, 2, 4)).forEach(pos -> {
            BlockState s = level.getBlockState(pos);
            if (s.getBlock() instanceof BonemealableBlock growable
                    && (s.is(BlockTags.CROPS) || s.is(BlockTags.SAPLINGS))) {
                if (growable.isValidBonemealTarget(level, pos, s, false)) {
                    growable.performBonemeal(level, level.random, pos, s);
                    level.sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0
                    );
                }
            }
        });
    }

    public boolean isActive() {
        return activationTime > 0 && level != null && level.getGameTime() >= activationTime;
    }

    public void activate(long gameTime) {
        activate(gameTime, Configuration.ASTRYLIS_DURATION.get());
    }

    public void activate(long gameTime, int customDuration) {
        this.activationTime = gameTime;
        this.duration = Math.max(1, customDuration);
        this.lastBonemealTime = -1;
        this.setChanged();
    }

    public void deactivate() {
        this.activationTime = -1;
        this.lastBonemealTime = -1;
        this.setChanged();
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        if (!isActive() || level == null) return 0.0f;
        long elapsed = level.getGameTime() - activationTime;
        return Math.min(1.0f, (float) elapsed / (float) duration);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong("activationTime", activationTime);
        tag.putInt("duration", duration);
        tag.putLong("lastBonemealTime", lastBonemealTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.activationTime = tag.getLong("activationTime");

        // Use saved duration if present; otherwise fall back to current config
        if (tag.contains("duration")) {
            this.duration = Math.max(1, tag.getInt("duration"));
        } else {
            this.duration = Math.max(1, Configuration.ASTRYLIS_DURATION.get());
        }

        this.lastBonemealTime = tag.getLong("lastBonemealTime");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putLong("activationTime", activationTime);
        tag.putInt("duration", duration);
        tag.putLong("lastBonemealTime", lastBonemealTime);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
