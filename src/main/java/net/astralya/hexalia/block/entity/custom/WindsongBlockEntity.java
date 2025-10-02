package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntities;
import net.astralya.hexalia.sound.ModSounds;
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

    private int  activeTicks      = 0;
    private long activationTime   = -1;
    private int  duration         = 0;
    private int  particleCooldown = 0;

    public WindsongBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDSONG_BE, pos, state);
    }

    private static int cfgDuration() {
        return Math.max(1, Configuration.common().plants.windsongDuration);
    }
    private static int cfgRadius() {
        return Math.max(1, Configuration.common().plants.windsongEffectRadius);
    }

    public void activate() {
        activate(cfgDuration());
    }

    public void activate(int customDuration) {
        this.duration       = Math.max(1, customDuration);
        this.activeTicks    = this.duration;
        this.activationTime = (this.world != null) ? this.world.getTime() : -1;
        markDirty();
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public int getDuration() {
        return duration;
    }

    public float getProgress() {
        return duration <= 0 ? 0.0f : Math.min(1.0f, (float) (duration - activeTicks) / duration);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.activationTime != -1 && world != null && this.activeTicks > 0 && this.duration > 0) {
            long elapsed   = world.getTime() - this.activationTime;
            int expected   = this.duration - (int) elapsed;
            if (Math.abs(this.activeTicks - expected) > 5) {
                this.activeTicks = Math.max(0, expected);
            }
        }

        if (!this.isActive()) return;

        this.activeTicks--;

        if (world instanceof ServerWorld server) {
            int radius = cfgRadius();

            Box area = new Box(pos).expand(radius);
            List<Entity> projectiles = server.getEntitiesByClass(Entity.class, area, e -> e instanceof ProjectileEntity);
            for (Entity projectile : projectiles) {
                if (!projectile.isRemoved()) {
                    discardProjectile(server, projectile);
                }
            }

            emitParticles(server, pos, radius);
        }

        if (this.activeTicks <= 0) {
            world.playSound(null, pos, ModSounds.WIND_BURST, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.breakBlock(pos, false);
        } else {
            markDirty();
        }
    }

    private void discardProjectile(ServerWorld world, Entity projectile) {
        world.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(),
                ModSounds.WIND_DEFLECT, SoundCategory.BLOCKS, 1.0f, 1.0f);

        Vec3d p = projectile.getPos();
        for (int i = 0; i < 5; i++) {
            double angle  = Math.random() * 2 * Math.PI;
            double r      = Math.random() * 0.5;
            double x      = p.x + r * Math.cos(angle);
            double z      = p.z + r * Math.sin(angle);
            double y      = p.y + Math.random() * 0.5;
            world.spawnParticles(ParticleTypes.EFFECT, x, y, z, 1, 0, 0, 0, 0.1);
        }

        projectile.discard();
    }

    private void emitParticles(ServerWorld world, BlockPos pos, int radius) {
        if (particleCooldown > 0) {
            particleCooldown--;
            return;
        }

        Vec3d c = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        float progress      = getProgress();
        int particleCount   = Math.max(1, (int) (3 * (1.0f - progress * 0.5f)));

        for (int i = 0; i < particleCount; i++) {
            double angle  = Math.random() * 2 * Math.PI;
            double r      = Math.random() * radius;
            double x      = c.x + r * Math.cos(angle);
            double z      = c.z + r * Math.sin(angle);
            double y      = c.y + Math.random() * 2;
            world.spawnParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0.1);
        }

        particleCooldown = 5;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activeTicks      = nbt.getInt("ActiveTicks");
        this.activationTime   = nbt.getLong("ActivationTime");
        this.duration         = nbt.contains("Duration") ? nbt.getInt("Duration") : cfgDuration();
        this.particleCooldown = nbt.getInt("ParticleCooldown");

        if (this.activationTime != -1 && this.world != null && this.activeTicks > 0 && this.duration > 0) {
            long elapsed = this.world.getTime() - this.activationTime;
            this.activeTicks = Math.max(0, this.duration - (int) elapsed);
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