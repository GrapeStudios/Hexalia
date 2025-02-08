package net.grapes.hexalia.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LunarLilyBlockEntity extends BlockEntity {

    private static final int DURATION = 1200;
    private static final int BONEMEAL_INTERVAL = 240;
    private long activationTime = -1;

    public LunarLilyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LUNAR_LILY_BE, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, LunarLilyBlockEntity entity) {
        if (world instanceof ServerWorld serverWorld && entity.isActive()) {
            long elapsedTime = world.getTime() - entity.activationTime;

            if (!isNight(world)) {
                entity.deactivate();
                return;
            }

            if (elapsedTime >= DURATION) {
                entity.deactivate();
                return;
            }

            if (elapsedTime % BONEMEAL_INTERVAL == 0) {
                applyBonemealToCropsAndSaplings(serverWorld, pos);
            }

            entity.markDirty();
        }
    }

    private static void applyBonemealToCropsAndSaplings(ServerWorld world, BlockPos centerPos) {
        BlockPos.iterate(centerPos.add(-4, -2, -4), centerPos.add(4, 2, 4)).forEach(pos -> {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof Fertilizable fertilizable &&
                    (state.isIn(BlockTags.CROPS) || state.isIn(BlockTags.SAPLINGS))) {

                if (fertilizable.isFertilizable(world, pos, state, false)) {
                    fertilizable.grow(world, world.getRandom(), pos, state);
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0);
                }
            }
        });
    }


    public boolean isActive() {
        return activationTime > 0 && world != null && world.getTime() >= activationTime;
    }

    public void activate(long gameTime) {
        this.activationTime = gameTime;
        this.markDirty();
    }

    public void deactivate() {
        this.activationTime = -1;
        this.markDirty();
    }

    private static boolean isNight(World world) {
        long time = world.getTimeOfDay();
        return time > 13000 && time < 23000;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("activationTime", activationTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activationTime = nbt.getLong("activationTime");
    }

}
