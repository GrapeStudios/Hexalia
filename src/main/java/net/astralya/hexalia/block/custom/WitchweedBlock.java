package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitchweedBlock extends FlowerBlock {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 1, 15, 7, 15);

    public WitchweedBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        if (living instanceof PlayerEntity p && p.getAbilities().creativeMode) return;
        if (living.isSneaking() || living instanceof FrogEntity || living instanceof SilkMothEntity || living instanceof BeeEntity) return;

        entity.slowMovement(state, new Vec3d(0.8F, 0.75D, 0.8F));

        if (!world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100));
        }
    }
}
