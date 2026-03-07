package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlightedBloomEffect implements ICenserEffect {

    private static final int PULSE_INTERVAL     = 32;
    private static final int MIN_CONVERSIONS    = 2;
    private static final int MAX_CONVERSIONS    = 4;
    private static final float MOOSHROOM_CHANCE = 0.10f;

    private static final double SPREAD_BIAS = 3.0;

    private int tickCounter = 0;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.blighted_bloom";
    }

    @Override
    public void onTick(ServerLevel level, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        spawnAmbientSpores(level, pos, radius);

        tickCounter++;
        if (tickCounter < PULSE_INTERVAL) return;
        tickCounter = 0;

        AABB area = new AABB(pos).inflate(radius);
        convertBlocks(level, pos, radius);
        trySpawnMushrooms(level, pos, radius);
        tryConvertCows(level, area);
    }

    private static void spawnAmbientSpores(ServerLevel level, BlockPos pos, int radius) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + (level.random.nextDouble() * 2 - 1) * radius;
            double y = pos.getY() + level.random.nextDouble() * (radius * 0.5);
            double z = pos.getZ() + (level.random.nextDouble() * 2 - 1) * radius;
            level.sendParticles(ParticleTypes.MYCELIUM, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static void convertBlocks(ServerLevel level, BlockPos center, int radius) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos min = BlockPos.containing(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.containing(center.getX() + radius, center.getY() + radius, center.getZ() + radius);

        for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(bp);
            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)) {
                if (isOnSpreadFrontier(level, bp, center)) {
                    candidates.add(bp.immutable());
                }
            }
        }

        if (candidates.isEmpty()) {
            for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
                BlockState state = level.getBlockState(bp);
                if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)) {
                    candidates.add(bp.immutable());
                }
            }
        }

        if (candidates.isEmpty()) return;

        candidates.sort(Comparator.comparingDouble(bp -> bp.distSqr(center)));

        int count = MIN_CONVERSIONS + level.random.nextInt(MAX_CONVERSIONS - MIN_CONVERSIONS + 1);
        count = Math.min(count, candidates.size());

        for (int i = 0; i < count; i++) {
            BlockPos target = pickBiasedNearest(candidates, level, radius);
            if (target == null) break;
            candidates.remove(target);
            level.setBlockAndUpdate(target, Blocks.MYCELIUM.defaultBlockState());
            spawnConversionParticles(level, target);
        }
    }

    private static boolean isOnSpreadFrontier(ServerLevel level, BlockPos bp, BlockPos center) {
        if (bp.closerThan(center, 2.0)) return true;
        for (BlockPos neighbor : neighbors(bp)) {
            if (level.getBlockState(neighbor).is(Blocks.MYCELIUM)) return true;
        }
        return false;
    }

    private static Iterable<BlockPos> neighbors(BlockPos pos) {
        List<BlockPos> result = new ArrayList<>(6);
        result.add(pos.north());
        result.add(pos.south());
        result.add(pos.east());
        result.add(pos.west());
        result.add(pos.above());
        result.add(pos.below());
        return result;
    }

    private static BlockPos pickBiasedNearest(List<BlockPos> sortedCandidates, ServerLevel level, int radius) {
        if (sortedCandidates.isEmpty()) return null;
        double totalWeight = 0.0;
        double[] weights = new double[sortedCandidates.size()];
        double maxDist = radius * radius;
        for (int i = 0; i < sortedCandidates.size(); i++) {
            double distSq = sortedCandidates.get(i).distSqr(sortedCandidates.get(0));
            weights[i] = Math.exp(-SPREAD_BIAS * distSq / Math.max(maxDist, 1));
            totalWeight += weights[i];
        }
        double roll = level.random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (int i = 0; i < sortedCandidates.size(); i++) {
            cumulative += weights[i];
            if (roll <= cumulative) return sortedCandidates.get(i);
        }
        return sortedCandidates.get(0);
    }

    private static void trySpawnMushrooms(ServerLevel level, BlockPos center, int radius) {
        if (level.random.nextFloat() > 0.5f) return;

        BlockPos min = BlockPos.containing(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        BlockPos max = BlockPos.containing(center.getX() + radius, center.getY() + radius, center.getZ() + radius);

        List<BlockPos> surfaces = new ArrayList<>();
        for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
            BlockState below = level.getBlockState(bp.below());
            BlockState current = level.getBlockState(bp);
            if ((below.is(Blocks.MYCELIUM) || below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.DIRT)) && current.isAir()) {
                surfaces.add(bp.immutable());
            }
        }

        if (surfaces.isEmpty()) return;

        surfaces.sort(Comparator.comparingDouble(bp -> bp.distSqr(center)));
        BlockPos target = pickBiasedNearest(surfaces, level, radius);
        if (target == null) return;

        BlockState mushroom = level.random.nextBoolean()
                ? Blocks.RED_MUSHROOM.defaultBlockState()
                : Blocks.BROWN_MUSHROOM.defaultBlockState();

        BlockPos ground = target.below();
        if (!level.getBlockState(ground).is(Blocks.MYCELIUM)) {
            level.setBlockAndUpdate(ground, Blocks.MYCELIUM.defaultBlockState());
            spawnConversionParticles(level, ground);
        }

        if (mushroom.canSurvive(level, target)) {
            level.setBlockAndUpdate(target, mushroom);
            level.playSound(null, target, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.4f, 0.8f + level.random.nextFloat() * 0.4f);
            spawnConversionParticles(level, target);
        }
    }

    private static void tryConvertCows(ServerLevel level, AABB area) {
        List<Cow> cows = level.getEntitiesOfClass(Cow.class, area,
                e -> !(e instanceof MushroomCow));

        for (Cow cow : cows) {
            if (level.random.nextFloat() > MOOSHROOM_CHANCE) continue;

            MushroomCow mooshroom = new MushroomCow(net.minecraft.world.entity.EntityType.MOOSHROOM, level);
            mooshroom.moveTo(cow.getX(), cow.getY(), cow.getZ(), cow.getYRot(), cow.getXRot());
            mooshroom.setHealth(cow.getHealth());
            mooshroom.setBaby(cow.isBaby());

            if (level.random.nextBoolean()) {
                mooshroom.setVariant(MushroomCow.MushroomType.BROWN);
            }

            level.addFreshEntity(mooshroom);
            cow.discard();

            level.sendParticles(ParticleTypes.MYCELIUM,
                    cow.getX(), cow.getY() + 1.0, cow.getZ(),
                    20, 0.5, 0.5, 0.5, 0.0);
            level.playSound(null, cow.blockPosition(),
                    SoundEvents.MOOSHROOM_CONVERT, SoundSource.NEUTRAL, 1.0f,
                    0.8f + level.random.nextFloat() * 0.4f);
        }
    }

    private static void spawnConversionParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.MYCELIUM,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                8, 0.4, 0.1, 0.4, 0.0);
        level.sendParticles(ParticleTypes.EFFECT,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                4, 0.2, 0.2, 0.2, 0.05);
    }
}