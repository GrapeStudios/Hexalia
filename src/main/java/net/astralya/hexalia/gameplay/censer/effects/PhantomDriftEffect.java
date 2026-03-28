package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PhantomDriftEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL = 32;
    private static final EntityEffectParticleEffect DRIFT_PARTICLE =
            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.45f, 0.1f, 0.7f);

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.phantom_drift";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);
        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        Box area = new Box(pos).expand(radius);
        List<BlockPos> containerPositions = findContainerPositions(world, pos, radius);
        if (containerPositions.isEmpty()) return;
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, area, e -> !e.isRemoved());
        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved()) continue;
            tryPhaseIntoContainer(world, itemEntity, containerPositions);
        }
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 2.0;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(DRIFT_PARTICLE, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static List<BlockPos> findContainerPositions(ServerWorld world, BlockPos center, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos min = BlockPos.ofFloored(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.ofFloored(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            for (Direction dir : new Direction[]{null, Direction.UP, Direction.DOWN, Direction.NORTH}) {
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, bp, dir);
                if (storage != null) {
                    positions.add(bp.toImmutable());
                    break;
                }
            }
        }
        return positions;
    }

    private static void tryPhaseIntoContainer(ServerWorld world, ItemEntity itemEntity,
                                              List<BlockPos> containerPositions) {
        BlockPos itemPos = BlockPos.ofFloored(itemEntity.getPos());
        containerPositions.sort(Comparator.comparingDouble(a -> a.getSquaredDistance(itemPos)));
        ItemStack remaining = itemEntity.getStack().copy();
        for (BlockPos containerPos : containerPositions) {
            Storage<ItemVariant> storage = getStorage(world, containerPos);
            if (storage == null) continue;
            remaining = insertItemStacked(storage, remaining, world);
            if (remaining.isEmpty()) break;
        }
        if (remaining.getCount() < itemEntity.getStack().getCount()) {
            spawnPhaseParticles(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            if (remaining.isEmpty()) {
                world.playSound(null, itemEntity.getBlockPos(),
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.3f,
                        1.4f + world.random.nextFloat() * 0.2f);
                itemEntity.remove(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            } else {
                itemEntity.setStack(remaining);
                world.playSound(null, itemEntity.getBlockPos(),
                        SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.2f,
                        1.4f + world.random.nextFloat() * 0.2f);
            }
        }
    }

    private static Storage<ItemVariant> getStorage(ServerWorld world, BlockPos pos) {
        for (Direction dir : new Direction[]{null, Direction.UP, Direction.DOWN, Direction.NORTH}) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, dir);
            if (storage != null) return storage;
        }
        return null;
    }

    private static ItemStack insertItemStacked(Storage<ItemVariant> storage, ItemStack stack, ServerWorld world) {
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            tx.commit();
            if (inserted >= stack.getCount()) return ItemStack.EMPTY;
            ItemStack remaining = stack.copy();
            remaining.setCount((int) (stack.getCount() - inserted));
            return remaining;
        }
    }

    private static void spawnPhaseParticles(ServerWorld world, double x, double y, double z) {
        world.spawnParticles(DRIFT_PARTICLE, x, y + 0.2, z, 8, 0.15, 0.15, 0.15, 0.04);
        world.spawnParticles(ParticleTypes.PORTAL, x, y + 0.2, z, 6, 0.15, 0.15, 0.15, 0.0);
    }
}