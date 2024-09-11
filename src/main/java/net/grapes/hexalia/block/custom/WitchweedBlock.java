package net.grapes.hexalia.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitchweedBlock extends FlowerBlock {
    protected static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.0625, 0.9375, 0.4375, 0.9375)
    );

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
        if (!world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity livingEntity) {
                if (!livingEntity.bypassesSteppingEffects() && !(livingEntity instanceof FrogEntity)) {
                    if (livingEntity instanceof PlayerEntity player && player.isCreative()) {
                        return;
                    }
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100));
                }
            }
        }
    }
}
