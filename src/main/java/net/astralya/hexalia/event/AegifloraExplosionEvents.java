package net.astralya.hexalia.event;

import net.astralya.hexalia.block.entity.custom.AegifloraBlockEntity;
import net.astralya.hexalia.particle.ModParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import org.jetbrains.annotations.Nullable;

public final class AegifloraExplosionEvents {

    private static final int RADIUS = 8;

    private AegifloraExplosionEvents() {
    }

    public static boolean onExplosionStart(ServerWorld world, Explosion explosion) {
        Entity source = explosion.getCausingEntity();
        if (!(source instanceof CreeperEntity)) {
            return false;
        }
        Vec3d center = explosion.getPosition();
        BlockPos origin = BlockPos.ofFloored(center);
        AegifloraBlockEntity aegiflora = findAegiflora(world, origin, RADIUS);
        if (aegiflora == null || !aegiflora.canAbsorb()) {
            return false;
        }
        double x = origin.getX() + 0.5;
        double y = origin.getY() + 0.5;
        double z = origin.getZ() + 0.5;
        AegifloraBlockEntity.AbsorbOutcome outcome = aegiflora.absorbOnce(world);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        spawnAegifloraParticles(world, aegiflora.getPos());
        switch (outcome) {
            case WITHERED -> world.playSound(null, x, y, z, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F);
            case DESTROYED -> world.playSound(null, x, y, z, SoundEvents.BLOCK_AZALEA_BREAK, SoundCategory.BLOCKS, 1.0F, 0.7F);
            default -> world.playSound(null, x, y, z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 1.2F);
        }
        sendPreventedMessage(world, origin, outcome);
        return true;
    }

    private static void sendPreventedMessage(ServerWorld world, BlockPos origin, AegifloraBlockEntity.AbsorbOutcome outcome) {
        String key = switch (outcome) {
            case WITHERED -> "message.hexalia.aegiflora.prevented.withered";
            case DESTROYED -> "message.hexalia.aegiflora.prevented.dead";
            default -> "message.hexalia.aegiflora.prevented";
        };
        Box area = new Box(origin).expand(RADIUS);
        for (ServerPlayerEntity player : world.getEntitiesByClass(ServerPlayerEntity.class, area, e -> true)) {
            player.sendMessage(Text.translatable(key), true);
        }
    }

    @Nullable
    private static AegifloraBlockEntity findAegiflora(ServerWorld world, BlockPos origin, int radius) {
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        int ox = origin.getX();
        int oy = origin.getY();
        int oz = origin.getZ();
        int r2 = radius * radius;
        for (int y = oy - radius; y <= oy + radius; y++) {
            for (int x = ox - radius; x <= ox + radius; x++) {
                for (int z = oz - radius; z <= oz + radius; z++) {
                    int dx = x - ox;
                    int dy = y - oy;
                    int dz = z - oz;
                    if ((dx * dx + dy * dy + dz * dz) > r2) continue;
                    cursor.set(x, y, z);
                    if (world.getBlockEntity(cursor) instanceof AegifloraBlockEntity be) {
                        return be;
                    }
                }
            }
        }
        return null;
    }

    private static void spawnAegifloraParticles(ServerWorld world, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.6;
        double cz = pos.getZ() + 0.5;
        world.spawnParticles(
                ModParticleType.LEAVES,
                cx, cy, cz,
                12,
                0.35, 0.25, 0.35,
                0.02
        );
    }
}