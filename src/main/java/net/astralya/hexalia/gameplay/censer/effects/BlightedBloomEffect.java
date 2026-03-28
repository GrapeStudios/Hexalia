package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlightedBloomEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL     = 32;
    private static final int MIN_CONVERSIONS    = 2;
    private static final int MAX_CONVERSIONS    = 4;
    private static final float MOOSHROOM_CHANCE = 0.10f;
    private static final double SPREAD_BIAS     = 3.0;

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.blighted_bloom";
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        spawnAmbientSpores(world, pos, radius);
        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;
        Box area = new Box(pos).expand(radius);
        convertBlocks(world, pos, radius);
        trySpawnMushrooms(world, pos, radius);
        tryConvertCows(world, area);
    }

    private static void spawnAmbientSpores(ServerWorld world, BlockPos pos, int radius) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (world.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + world.random.nextDouble() * (radius * 0.5);
            double z = pos.getZ() + (world.random.nextDouble() * 2 - 1) * radius;
            world.spawnParticles(ParticleTypes.MYCELIUM, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void convertBlocks(ServerWorld world, BlockPos center, int radius) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos min = BlockPos.ofFloored(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.ofFloored(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            BlockState state = world.getBlockState(bp);
            if (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT)) {
                if (isOnSpreadFrontier(world, bp, center)) {
                    candidates.add(bp.toImmutable());
                }
            }
        }
        if (candidates.isEmpty()) {
            for (BlockPos bp : BlockPos.iterate(min, max)) {
                BlockState state = world.getBlockState(bp);
                if (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT)) {
                    candidates.add(bp.toImmutable());
                }
            }
        }
        if (candidates.isEmpty()) return;
        candidates.sort(Comparator.comparingDouble(bp -> bp.getSquaredDistance(center)));
        int count = MIN_CONVERSIONS + world.random.nextInt(MAX_CONVERSIONS - MIN_CONVERSIONS + 1);
        count = Math.min(count, candidates.size());
        for (int i = 0; i < count; i++) {
            BlockPos target = pickBiasedNearest(candidates, world, radius);
            if (target == null) break;
            candidates.remove(target);
            world.setBlockState(target, Blocks.MYCELIUM.getDefaultState());
            spawnConversionParticles(world, target);
        }
    }

    private static boolean isOnSpreadFrontier(ServerWorld world, BlockPos bp, BlockPos center) {
        if (bp.isWithinDistance(center, 2.0)) return true;
        for (BlockPos neighbor : neighbors(bp)) {
            if (world.getBlockState(neighbor).isOf(Blocks.MYCELIUM)) return true;
        }
        return false;
    }

    private static Iterable<BlockPos> neighbors(BlockPos pos) {
        List<BlockPos> result = new ArrayList<>(6);
        result.add(pos.north());
        result.add(pos.south());
        result.add(pos.east());
        result.add(pos.west());
        result.add(pos.up());
        result.add(pos.down());
        return result;
    }

    private static BlockPos pickBiasedNearest(List<BlockPos> sortedCandidates, ServerWorld world, int radius) {
        if (sortedCandidates.isEmpty()) return null;
        double totalWeight = 0.0;
        double[] weights = new double[sortedCandidates.size()];
        double maxDist = radius * radius;
        for (int i = 0; i < sortedCandidates.size(); i++) {
            double distSq = sortedCandidates.get(i).getSquaredDistance(sortedCandidates.get(0));
            weights[i] = Math.exp(-SPREAD_BIAS * distSq / Math.max(maxDist, 1));
            totalWeight += weights[i];
        }
        double roll = world.random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (int i = 0; i < sortedCandidates.size(); i++) {
            cumulative += weights[i];
            if (roll <= cumulative) return sortedCandidates.get(i);
        }
        return sortedCandidates.get(0);
    }

    private static void trySpawnMushrooms(ServerWorld world, BlockPos center, int radius) {
        if (world.random.nextFloat() > 0.5f) return;
        BlockPos min = BlockPos.ofFloored(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.ofFloored(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        List<BlockPos> surfaces = new ArrayList<>();
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            BlockState below = world.getBlockState(bp.down());
            BlockState current = world.getBlockState(bp);
            if ((below.isOf(Blocks.MYCELIUM) || below.isOf(Blocks.GRASS_BLOCK) || below.isOf(Blocks.DIRT)) && current.isAir()) {
                surfaces.add(bp.toImmutable());
            }
        }
        if (surfaces.isEmpty()) return;
        surfaces.sort(Comparator.comparingDouble(bp -> bp.getSquaredDistance(center)));
        BlockPos target = pickBiasedNearest(surfaces, world, radius);
        if (target == null) return;
        BlockState mushroom = world.random.nextBoolean()
                ? Blocks.RED_MUSHROOM.getDefaultState()
                : Blocks.BROWN_MUSHROOM.getDefaultState();
        BlockPos ground = target.down();
        if (!world.getBlockState(ground).isOf(Blocks.MYCELIUM)) {
            world.setBlockState(ground, Blocks.MYCELIUM.getDefaultState());
            spawnConversionParticles(world, ground);
        }
        if (mushroom.canPlaceAt(world, target)) {
            world.setBlockState(target, mushroom);
            world.playSound(null, target, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS,
                    0.4f, 0.8f + world.random.nextFloat() * 0.4f);
            spawnConversionParticles(world, target);
        }
    }

    private static void tryConvertCows(ServerWorld world, Box area) {
        List<CowEntity> cows = world.getEntitiesByClass(CowEntity.class, area,
                e -> !(e instanceof MooshroomEntity));
        for (CowEntity cow : cows) {
            if (world.random.nextFloat() > MOOSHROOM_CHANCE) continue;
            MooshroomEntity mooshroom = new MooshroomEntity(EntityType.MOOSHROOM, world);
            mooshroom.refreshPositionAndAngles(cow.getX(), cow.getY(), cow.getZ(), cow.getYaw(), cow.getPitch());
            mooshroom.setHealth(cow.getHealth());
            mooshroom.setBaby(cow.isBaby());
            if (world.random.nextBoolean()) {
                mooshroom.setVariant(MooshroomEntity.Type.BROWN);
            }
            world.spawnEntity(mooshroom);
            cow.remove(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            world.spawnParticles(ParticleTypes.MYCELIUM,
                    cow.getX(), cow.getY() + 1.0, cow.getZ(),
                    20, 0.5, 0.5, 0.5, 0.0);
            world.playSound(null, cow.getBlockPos(),
                    SoundEvents.ENTITY_MOOSHROOM_CONVERT, SoundCategory.NEUTRAL, 1.0f,
                    0.8f + world.random.nextFloat() * 0.4f);
        }
    }

    private static void spawnConversionParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(ParticleTypes.MYCELIUM,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                8, 0.4, 0.1, 0.4, 0.0);
        world.spawnParticles(ParticleTypes.EFFECT,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                4, 0.2, 0.2, 0.2, 0.05);
    }
}
