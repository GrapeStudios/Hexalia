package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.effect.ModEffectCure;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class LourdesBlockEntity extends BlockEntity {

    private static final int DURATION_TICKS = Configuration.LOURDES_DURATION.get();
    private static final int PULSE_INTERVAL_TICKS = 20;
    private static final double RADIUS = Configuration.LOURDES_EFFECT_RADIUS.get();

    private long activeUntilGameTime;

    public LourdesBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LOURDES.get(), pos, state);
    }

    public boolean isActive() {
        if (this.level == null) {
            return false;
        }
        return this.activeUntilGameTime > this.level.getGameTime();
    }

    public void activate(long gameTime) {
        this.activeUntilGameTime = gameTime + DURATION_TICKS;
        this.setChanged();

        if (this.level != null) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LourdesBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        long gameTime = level.getGameTime();

        if (be.activeUntilGameTime != 0L && gameTime >= be.activeUntilGameTime) {
            be.activeUntilGameTime = 0L;
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        if (be.activeUntilGameTime == 0L) {
            return;
        }

        if ((gameTime % PULSE_INTERVAL_TICKS) != 0L) {
            return;
        }

        AABB aabb = new AABB(pos).inflate(RADIUS);
        List<LivingEntity> targets = new ArrayList<>();
        targets.addAll(level.getEntitiesOfClass(Player.class, aabb, LivingEntity::isAlive));
        targets.addAll(level.getEntitiesOfClass(Animal.class, aabb, LivingEntity::isAlive));

        for (LivingEntity target : targets) {
            cleanseEffects(target);
            applyHealingAura(target);
        }
    }

    private static void cleanseEffects(LivingEntity entity) {
        entity.removeEffectsCuredBy(ModEffectCure.PURIFYING);
    }

    private static void applyHealingAura(LivingEntity entity) {
        MobEffectInstance existing = entity.getEffect(MobEffects.REGENERATION);
        if (existing == null || existing.getDuration() < 30) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, true, true));
        }
    }

    public void spawnActiveParticles(Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.35D;
        double z = pos.getZ() + 0.5D;

        for (int i = 0; i < 3; i++) {
            double ox = (random.nextDouble() - 0.5D) * 0.6D;
            double oy = random.nextDouble() * 0.35D;
            double oz = (random.nextDouble() - 0.5D) * 0.6D;

            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x + ox, y + oy, z + oz, 0.0D, 0.02D, 0.0D);
            if ((random.nextInt(3)) == 0) {
                level.addParticle(ModParticleType.LEAVES.get(), x + ox, y + oy, z + oz, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("ActiveUntil", this.activeUntilGameTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.activeUntilGameTime = tag.getLong("ActiveUntil");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putLong("ActiveUntil", this.activeUntilGameTime);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
