package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TidesMemoryEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL    = 32;
    private static final float GUARDIAN_CHANCE = 0.10f;

    private record LootEntry(ItemStack stack, int weight) {}

    private static final List<LootEntry> LOOT_TABLE = List.of(
            new LootEntry(new ItemStack(Items.COD, 2),              40),
            new LootEntry(new ItemStack(Items.SALMON),              30),
            new LootEntry(new ItemStack(Items.KELP, 3),             35),
            new LootEntry(new ItemStack(Items.SEAGRASS, 2),         30),
            new LootEntry(new ItemStack(Items.NAUTILUS_SHELL),      12),
            new LootEntry(new ItemStack(Items.PRISMARINE_SHARD, 2), 15),
            new LootEntry(new ItemStack(Items.PRISMARINE_CRYSTALS), 12),
            new LootEntry(new ItemStack(Items.TURTLE_SCUTE),         8)
    );

    private static final int TOTAL_WEIGHT = LOOT_TABLE.stream()
            .mapToInt(LootEntry::weight).sum();

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.tides_memory";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        spawnAmbientParticles(world, pos);
        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;
        BlockPos waterSurface = findWaterSurface(world, pos);
        if (waterSurface != null) {
            spawnLoot(world, waterSurface);
            spawnSurfaceParticles(world, waterSurface);
            world.playSound(null, waterSurface, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH,
                    SoundCategory.BLOCKS, 0.5f, 0.8f + world.random.nextFloat() * 0.4f);
        }
        if (world.random.nextFloat() < GUARDIAN_CHANCE) {
            trySpawnGuardian(world, pos);
        }
    }

    private static BlockPos findWaterSurface(ServerWorld world, BlockPos center) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        List<BlockPos> surfaces = new ArrayList<>();
        BlockPos min = BlockPos.ofFloored(center.getX() - radius, center.getY() - 4, center.getZ() - radius);
        BlockPos max = BlockPos.ofFloored(center.getX() + radius, center.getY() + 4, center.getZ() + radius);
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            if (world.getBlockState(bp).isOf(Blocks.WATER)
                    && world.getBlockState(bp.up()).isAir()) {
                surfaces.add(bp.toImmutable());
            }
        }
        if (surfaces.isEmpty()) return null;
        return surfaces.get(world.random.nextInt(surfaces.size()));
    }

    private static void spawnLoot(ServerWorld world, BlockPos surface) {
        ItemStack drop = rollLoot(world);
        double x = surface.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 0.4;
        double y = surface.getY() + 0.8;
        double z = surface.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 0.4;
        ItemEntity itemEntity = new ItemEntity(world, x, y, z, drop);
        itemEntity.setVelocity(0, 0.2 + world.random.nextDouble() * 0.1, 0);
        world.spawnEntity(itemEntity);
    }

    private static ItemStack rollLoot(ServerWorld world) {
        int roll = world.random.nextInt(TOTAL_WEIGHT);
        int cumulative = 0;
        for (LootEntry entry : LOOT_TABLE) {
            cumulative += entry.weight();
            if (roll < cumulative) return entry.stack().copy();
        }
        return new ItemStack(Items.COD);
    }

    private static void trySpawnGuardian(ServerWorld world, BlockPos center) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        List<BlockPos> waterBlocks = new ArrayList<>();
        BlockPos min = BlockPos.ofFloored(center.getX() - radius, center.getY() - 4, center.getZ() - radius);
        BlockPos max = BlockPos.ofFloored(center.getX() + radius, center.getY() + 4, center.getZ() + radius);
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            if (world.getBlockState(bp).isOf(Blocks.WATER)) {
                waterBlocks.add(bp.toImmutable());
            }
        }
        if (waterBlocks.isEmpty()) return;
        BlockPos spawnPos = waterBlocks.get(world.random.nextInt(waterBlocks.size()));
        GuardianEntity guardian = new GuardianEntity(EntityType.GUARDIAN, world);
        guardian.refreshPositionAndAngles(Vec3d.ofCenter(spawnPos).x, Vec3d.ofCenter(spawnPos).y, Vec3d.ofCenter(spawnPos).z, 0, 0);
        guardian.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.TRIGGERED, null);
        world.spawnEntityAndPassengers(guardian);
        world.playSound(null, spawnPos, SoundEvents.ENTITY_GUARDIAN_AMBIENT,
                SoundCategory.HOSTILE, 1.0f, 1.0f);
    }

    private static void spawnAmbientParticles(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * 1.5;
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(ParticleTypes.DRIPPING_WATER, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void spawnSurfaceParticles(ServerWorld world, BlockPos surface) {
        world.spawnParticles(ParticleTypes.SPLASH,
                surface.getX() + 0.5, surface.getY() + 1.0, surface.getZ() + 0.5,
                12, 0.3, 0.1, 0.3, 0.1);
        world.spawnParticles(ParticleTypes.FISHING,
                surface.getX() + 0.5, surface.getY() + 1.0, surface.getZ() + 0.5,
                6, 0.2, 0.1, 0.2, 0.05);
    }
}
