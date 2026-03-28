package net.astralya.hexalia.gameplay.censer.effects;

import net.astralya.hexalia.Configuration;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class MinersRespiteEffect implements ICenserEffect {

    private static final int EFFECT_REFRESH_TICKS = 300;

    @Override
    public String getMessageKey() {
        return "message.hexalia.censer.miners_respite";
    }

    @Override
    public void onStart(ServerWorld world, BlockPos pos) {
        repairAnvilsInArea(world, pos, Configuration.CENSER_EFFECT_RADIUS.get());
    }

    @Override
    public void onTick(ServerWorld world, BlockPos pos) {
        int radius = Configuration.CENSER_EFFECT_RADIUS.get();
        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(radius), e -> true)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, EFFECT_REFRESH_TICKS, 0, false, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, EFFECT_REFRESH_TICKS, 1, false, false, true));
        }
    }

    private static void repairAnvilsInArea(ServerWorld world, BlockPos center, int radius) {
        Box area = new Box(center).expand(radius);
        BlockPos min = BlockPos.ofFloored(area.minX, area.minY, area.minZ);
        BlockPos max = BlockPos.ofFloored(area.maxX, area.maxY, area.maxZ);
        for (BlockPos bp : BlockPos.iterate(min, max)) {
            BlockState state = world.getBlockState(bp);
            if (!(state.getBlock() instanceof AnvilBlock)) continue;
            Direction facing = state.contains(Properties.HORIZONTAL_FACING) ? state.get(Properties.HORIZONTAL_FACING) : Direction.NORTH;
            BlockState repaired = null;
            if (state.isOf(Blocks.DAMAGED_ANVIL)) repaired = Blocks.CHIPPED_ANVIL.getDefaultState();
            else if (state.isOf(Blocks.CHIPPED_ANVIL)) repaired = Blocks.ANVIL.getDefaultState();
            if (repaired == null) continue;
            if (repaired.contains(Properties.HORIZONTAL_FACING)) repaired = repaired.with(Properties.HORIZONTAL_FACING, facing);
            world.setBlockState(bp, repaired, 3);
            world.playSound(null, bp, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.35f, 1.2f);
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, bp.getX() + 0.5, bp.getY() + 1.0, bp.getZ() + 0.5, 3, 0.25, 0.15, 0.25, 0.0);
        }
    }
}
