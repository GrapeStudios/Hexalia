package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PhantomDriftEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL = 32;

    private static final ColorParticleOption DRIFT_PARTICLE =
            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.45f, 0.1f, 0.7f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.phantom_drift";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        spawnAmbientParticles(level, pos);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;

        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        AABB area = new AABB(pos).inflate(radius);

        List<BlockPos> containerPositions = findContainerPositions(level, pos, radius);
        if (containerPositions.isEmpty()) return;

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area,
                e -> !e.isRemoved());

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved()) continue;
            tryPhaseIntoContainer(level, itemEntity, containerPositions);
        }
    }

    private static void spawnAmbientParticles(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * 2.0;
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(DRIFT_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static List<BlockPos> findContainerPositions(ServerLevel level, BlockPos center, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos min = BlockPos.containing(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.containing(center.getX() + radius, center.getY() + radius, center.getZ() + radius);

        for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
            for (Direction dir : new Direction[]{null, Direction.UP, Direction.DOWN, Direction.NORTH}) {
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, bp, dir);
                if (handler != null) {
                    positions.add(bp.immutable());
                    break;
                }
            }
        }
        return positions;
    }

    private static void tryPhaseIntoContainer(ServerLevel level, ItemEntity itemEntity,
                                              List<BlockPos> containerPositions) {
        BlockPos itemPos = BlockPos.containing(itemEntity.position());
        containerPositions.sort(Comparator.comparingDouble(a -> a.distSqr(itemPos)));

        ItemStack remaining = itemEntity.getItem().copy();

        for (BlockPos containerPos : containerPositions) {
            IItemHandler handler = getHandler(level, containerPos);
            if (handler == null) continue;
            remaining = ItemHandlerHelper.insertItemStacked(handler, remaining, false);
            if (remaining.isEmpty()) break;
        }

        if (remaining.getCount() < itemEntity.getItem().getCount()) {
            spawnPhaseParticles(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            if (remaining.isEmpty()) {
                level.playSound(null, itemEntity.blockPosition(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.3f,
                        1.4f + level.random.nextFloat() * 0.2f);
                itemEntity.discard();
            } else {
                itemEntity.setItem(remaining);
                level.playSound(null, itemEntity.blockPosition(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.2f,
                        1.4f + level.random.nextFloat() * 0.2f);
            }
        }
    }

    private static IItemHandler getHandler(ServerLevel level, BlockPos pos) {
        for (Direction dir : new Direction[]{null, Direction.UP, Direction.DOWN, Direction.NORTH}) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, dir);
            if (handler != null) return handler;
        }
        return null;
    }

    private static void spawnPhaseParticles(ServerLevel level, double x, double y, double z) {
        level.sendParticles(DRIFT_PARTICLE, x, y + 0.2, z, 8, 0.15, 0.15, 0.15, 0.04);
        level.sendParticles(ParticleTypes.PORTAL, x, y + 0.2, z, 6, 0.15, 0.15, 0.15, 0.0);
    }
}