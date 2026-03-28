package net.astralya.hexalia.gameplay.censer.effects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface ICenserEffect {

    String getMessageKey();

    default boolean usesSpatialCache() { return false; }

    default void onStart(ServerWorld world, BlockPos pos) {}

    void onTick(ServerWorld world, BlockPos pos);

    default void onStop(ServerWorld world, BlockPos pos) {}
}