package net.grapes.hexalia.block.entity;

import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WindsongBlockEntity extends BlockEntity {

    private int activeTicks = 0;
    private long activationTime = -1;
    private int duration = 600;
    private static final int DEFAULT_DURATION = 600;
    private static final int AREA_RADIUS = 6;
    private int particleCooldown = 0;

    public WindsongBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDSONG_BE, pos, state);
    }

    public void activate() {
        activate(DEFAULT_DURATION);
    }

    public void activate(int customDuration) {
        this.activeTicks = customDuration;
        this.duration = customDuration;
        if (this.world != null) {
            this.activationTime = this.world.getTime();
        }
        markDirty();
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        if (duration <= 0) {
            return 0.0f;
        }
        return Math.min(1.0f, (float) (duration - activeTicks) / duration);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.isActive()) {
            this.activeTicks--;

            if (world instanceof ServerWorld serverWorld) {
                Box area = new Box(pos).expand(AREA_RADIUS);
                List<Entity> projectiles = serverWorld.getEntitiesByClass(Entity.class, area,
                        entity -> entity instanceof ProjectileEntity);

                for (Entity projectile : projectiles) {
                    if (!projectile.isRemoved()) {
                        discardProjectile(serverWorld, projectile);
                    }
                }

                emitParticles(serverWorld, pos);
            }

            if (this.activeTicks <= 0) {
                world.playSound(null, pos, ModSounds.WIND_BURST, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.breakBlock(pos, false);
            }
        }
    }

    private void discardProjectile(ServerWorld world, Entity projectile) {
        world.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(),
                ModSounds.WIND_DEFLECT, SoundCategory.BLOCKS, 1.0f, 1.0f);

        Vec3d pos = projectile.getPos();
        for (int i = 0; i < 5; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random() * 0.5;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y + Math.random() * 0.5;

            world.spawnParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }

        projectile.discard();
    }

    private void emitParticles(ServerWorld world, BlockPos pos) {
        if (particleCooldown <= 0) {
            Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            float progress = getProgress();
            int particleCount = Math.max(1, (int)(3 * (1.0f - progress * 0.5f)));

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double radius = Math.random() * AREA_RADIUS;
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                double y = center.y + Math.random() * 2;
                world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0.1);
            }
            particleCooldown = 5;
        } else {
            particleCooldown--;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activeTicks = nbt.getInt("ActiveTicks");
        this.activationTime = nbt.getLong("ActivationTime");
        this.duration = nbt.contains("Duration") ? nbt.getInt("Duration") : DEFAULT_DURATION;
        this.particleCooldown = nbt.getInt("ParticleCooldown");

        if (this.activationTime != -1 && this.world != null && this.activeTicks > 0) {
            long currentTime = this.world.getTime();
            long elapsed = currentTime - this.activationTime;
            this.activeTicks = Math.max(0, this.duration - (int)elapsed);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
        nbt.putInt("Duration", duration);
        nbt.putInt("ParticleCooldown", particleCooldown);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
        nbt.putInt("Duration", duration);
        nbt.putInt("ParticleCooldown", particleCooldown);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}