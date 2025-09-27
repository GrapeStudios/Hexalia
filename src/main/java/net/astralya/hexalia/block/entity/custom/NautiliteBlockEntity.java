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
import net.minecraft.registry.RegistryWrapper;
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
        return Math.max(1, Configuration.common().plants.nautiliteDuration);
    }
    private static int effectRadius() {
        return Math.max(1, Configuration.common().plants.nautiliteEffectRadius);
    }

    public void activate() {
        this.activeTicks = duration();
        if (this.world != null) {
            this.activationTime = this.world.getTime();
        }
        markDirty();
    }

    public boolean isActive() {
        return this.activeTicks > 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, NautiliteBlockEntity be) {
        if (be.activationTime != -1 && world != null) {
            long now     = world.getTime();
            long elapsed = now - be.activationTime;
            int expected = duration() - (int) elapsed;
            if (Math.abs(be.activeTicks - expected) > 5) {
                be.activeTicks = Math.max(0, expected);
            }
        }

        if (!be.isActive()) return;

        be.activeTicks--;

        if (world instanceof ServerWorld server) {
            int r = effectRadius();
            Box area = new Box(pos).expand(r);

            List<PlayerEntity> players = server.getEntitiesByClass(PlayerEntity.class, area, e -> true);
            for (PlayerEntity p : players) {
                if (p.isTouchingWaterOrRain()) {
                    p.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 40, 0, true, false));
                }
                if (p.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                    p.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                }
            }

            List<LivingEntity> mobs = server.getEntitiesByClass(LivingEntity.class, area, e -> true);
            for (LivingEntity mob : mobs) {
                if ((mob instanceof DrownedEntity || mob instanceof GuardianEntity)
                        && mob.isTouchingWaterOrRain()
                        && pos.isWithinDistance(mob.getBlockPos(), r)) {
                    mob.damage(world.getDamageSources().magic(), 2.0F);
                    server.spawnParticles(ParticleTypes.BUBBLE, mob.getX(), mob.getY(), mob.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }

            emitParticles(server, pos);
        }

        if (be.activeTicks <= 0) {
            assert world != null;
            world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.removeBlock(pos, false);
        } else {
            be.markDirty();
        }
    }

    private static void emitParticles(ServerWorld world, BlockPos pos) {
        Vec3d c = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Random rand = world.getRandom();
        for (int i = 0; i < 5; i++) {
            double x = c.x + (rand.nextDouble() - 0.5) * 2;
            double y = c.y + (rand.nextDouble() - 0.5) * 2;
            double z = c.z + (rand.nextDouble() - 0.5) * 2;
            world.spawnParticles(ParticleTypes.BUBBLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.activeTicks    = nbt.getInt("ActiveTicks");
        this.activationTime = nbt.getLong("ActivationTime");

        if (this.activationTime != -1 && this.world != null && this.activeTicks > 0) {
            long now = this.world.getTime();
            long elapsed = now - this.activationTime;
            this.activeTicks = Math.max(0, duration() - (int) elapsed);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("ActiveTicks", activeTicks);
        nbt.putLong("ActivationTime", activationTime);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registryLookup);
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
