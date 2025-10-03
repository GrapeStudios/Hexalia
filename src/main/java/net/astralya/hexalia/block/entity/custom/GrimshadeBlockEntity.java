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
        int v = Configuration.common().plants.grimshadeDuration;
        return Math.max(1, v <= 0 ? 2400 : v);
    }

    private static int effectRadius() {
        int v = Configuration.common().plants.grimshadeEffectRadius;
        return Math.max(1, v <= 0 ? 8 : v);
    }

    public void activate() {
        this.activeTicks = duration();
        if (this.world != null) this.activationTime = this.world.getTime();
        markDirty();
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, GrimshadeBlockEntity be) {
        if (be.activationTime != -1 && world != null) {
            long now = world.getTime();
            long elapsed = now - be.activationTime;
            int expected = duration() - (int) elapsed;
            if (Math.abs(be.activeTicks - expected) > 5) {
                be.activeTicks = Math.max(0, expected);
            }
        }

        if (!be.isActive()) return;

        be.activeTicks--;

        if (world instanceof ServerWorld server) {
            if (server.getDifficulty() != Difficulty.PEACEFUL) {
                int r = effectRadius();
                Box area = new Box(pos).expand(r);
                List<LivingEntity> targets = server.getEntitiesByClass(LivingEntity.class, area, e -> e.isAlive() && !(e instanceof PlayerEntity));
                for (LivingEntity mob : targets) {
                    mob.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 0, true, true));
                    mob.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, true, true));
                }
            }
            emitParticles(server, pos);
        }

        if (be.activeTicks <= 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 0.6f, 0.7f);
            be.markDirty();
        } else {
            be.markDirty();
        }
    }

    public void applyCollisionPing(LivingEntity e) {
        if (this.world == null || this.world.isClient || this.world.getDifficulty() == Difficulty.PEACEFUL) return;
        if (e instanceof PlayerEntity) return;
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 0, true, true));
        e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, true, true));
    }

    private static void emitParticles(ServerWorld world, BlockPos pos) {
        if (world.getRandom().nextInt(3) != 0) return;

        Vec3d c = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
        Random rand = world.getRandom();
        int count = 6 + rand.nextInt(4);

        for (int i = 0; i < count; i++) {
            double ox = (rand.nextDouble() - 0.5) * 0.6;
            double oy = rand.nextDouble() * 0.5;
            double oz = (rand.nextDouble() - 0.5) * 0.6;

            world.spawnParticles(ParticleTypes.SMOKE, c.x + ox, c.y + oy, c.z + oz, 1, 0, 0.001, 0, 0.0);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activeTicks = nbt.getInt("ActiveTicks");
        this.activationTime = nbt.getLong("ActivationTime");
        if (this.activationTime != -1 && this.world != null && this.activeTicks > 0) {
            long now = this.world.getTime();
            long elapsed = now - this.activationTime;
            this.activeTicks = Math.max(0, duration() - (int) elapsed);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}