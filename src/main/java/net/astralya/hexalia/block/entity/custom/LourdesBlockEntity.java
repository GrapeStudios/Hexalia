package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LourdesBlockEntity extends BlockEntity {

    private static final int DURATION_TICKS = Configuration.LOURDES_DURATION.get();
    private static final int PULSE_INTERVAL_TICKS = 20;
    private static final double RADIUS = Configuration.LOURDES_EFFECT_RADIUS.get();

    private long activeUntilGameTime;

    public LourdesBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LOURDES, pos, state);
    }

    public boolean isActive() {
        if (this.world == null) return false;
        return this.activeUntilGameTime > this.world.getTime();
    }

    public void activate(long gameTime) {
        this.activeUntilGameTime = gameTime + DURATION_TICKS;
        this.markDirty();
        if (this.world != null) {
            BlockState state = this.getCachedState();
            this.world.updateListeners(this.pos, state, state, 3);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, LourdesBlockEntity be) {
        if (world.isClient) return;
        long gameTime = world.getTime();
        if (be.activeUntilGameTime != 0L && gameTime >= be.activeUntilGameTime) {
            be.activeUntilGameTime = 0L;
            be.markDirty();
            world.updateListeners(pos, state, state, 3);
            return;
        }
        if (be.activeUntilGameTime == 0L) return;
        if ((gameTime % PULSE_INTERVAL_TICKS) != 0L) return;
        Box box = new Box(pos).expand(RADIUS);
        List<LivingEntity> targets = new ArrayList<>();
        targets.addAll(world.getEntitiesByClass(PlayerEntity.class, box, LivingEntity::isAlive));
        targets.addAll(world.getEntitiesByClass(AnimalEntity.class, box, LivingEntity::isAlive));
        for (LivingEntity target : targets) {
            cleanseEffects(target);
            applyHealingAura(target);
        }
    }

    private static void cleanseEffects(LivingEntity entity) {
        ModUtil.removeHarmfulEffects(entity);
    }

    private static void applyHealingAura(LivingEntity entity) {
        StatusEffectInstance existing = entity.getStatusEffect(StatusEffects.REGENERATION);
        if (existing == null || existing.getDuration() < 30) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 0, true, true, true));
        }
    }

    public void spawnActiveParticles(World world, BlockPos pos, Random random) {
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.35D;
        double cz = pos.getZ() + 0.5D;
        float r = 0.95F;
        float g = 0.45F;
        float b = 0.75F;
        for (int i = 0; i < 4; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double dist = 0.8D + random.nextDouble() * 2.2D;
            double x = cx + Math.cos(angle) * dist;
            double z = cz + Math.sin(angle) * dist;
            double y = cy + random.nextDouble() * 0.6D;
            double vx = (random.nextDouble() - 0.5D) * 0.01D;
            double vy = 0.01D + random.nextDouble() * 0.02D;
            double vz = (random.nextDouble() - 0.5D) * 0.01D;
            world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), x, y, z, vx, vy, vz);
            world.addParticle(ModParticleType.SPARKLE, x, y, z, 0, 0.01, 0);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putLong("ActiveUntil", this.activeUntilGameTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.activeUntilGameTime = nbt.getLong("ActiveUntil");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putLong("ActiveUntil", this.activeUntilGameTime);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}