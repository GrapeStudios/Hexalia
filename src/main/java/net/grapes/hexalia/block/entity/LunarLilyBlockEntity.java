package net.grapes.hexalia.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LunarLilyBlockEntity extends BlockEntity {

    private static final int DURATION = 1200;
    private static final int BONEMEAL_INTERVAL = 240;

    private long activationTime = -1;

    public LunarLilyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LUNAR_LILY_BE.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LunarLilyBlockEntity entity) {
        if (level instanceof ServerLevel serverLevel && entity.isActive()) {
            long elapsedTime = level.getGameTime() - entity.activationTime;

            if (!isNight(level)) {
                entity.deactivate();
                return;
            }

            if (elapsedTime >= DURATION) {
                entity.deactivate();
                return;
            }

            if (elapsedTime % BONEMEAL_INTERVAL == 0) {
                applyBonemealToCropsAndSaplings(serverLevel, pos);
            }

            entity.setChanged();
        }
    }


    private static void applyBonemealToCropsAndSaplings(ServerLevel level, BlockPos centerPos) {
        BlockPos.betweenClosedStream(centerPos.offset(-4, -2, -4), centerPos.offset(4, 2, 4)).forEach(pos -> {
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof BonemealableBlock bonemealableBlock &&
                    (state.is(BlockTags.CROPS) || state.is(BlockTags.SAPLINGS))) {

                if (bonemealableBlock.isValidBonemealTarget(level, pos, state, false)) {
                    bonemealableBlock.performBonemeal(level, level.random, pos, state);
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            1, 0.2, 0.2, 0.2, 0.0);
                }
            }
        });
    }

    public boolean isActive() {
        return activationTime > 0 && level != null && level.getGameTime() >= activationTime;
    }

    public void activate(long gameTime) {
        this.activationTime = gameTime;
        this.setChanged();
    }

    public void deactivate() {
        this.activationTime = -1;
        this.setChanged();
    }

    private static boolean isNight(Level level) {
        long time = level.getDayTime() % 24000;
        return time >= 13000 && time <= 23000;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putLong("activationTime", activationTime);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.activationTime = pTag.getLong("activationTime");
    }

}
