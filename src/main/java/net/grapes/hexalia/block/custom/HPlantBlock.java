package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class HPlantBlock extends FlowerBlock {
    public HPlantBlock(Supplier<MobEffect> effectSupplier, int pEffectDuration, Properties pProperties) {
        super(effectSupplier, pEffectDuration, pProperties);
    }

     /* Hexalia's HFlowerBlock can be used for flowers blocks that can be applied
    with Bone Meal when planted in an Infused Dirt block to duplicate the flower.  */

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(BlockTags.DIRT) || floor.is(ModBlocks.INFUSED_DIRT.get());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        if (!blockBelow.is(ModBlocks.INFUSED_DIRT.get())) {
            return InteractionResult.PASS;
        }

        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.is(Items.BONE_MEAL)) {
            if (!pLevel.isClientSide()) {
                if (this.canGrow(pLevel, pPos)) {
                    popResource(pLevel, pPos, new ItemStack(this));
                    if (!pPlayer.isCreative()) {
                        itemStack.shrink(1);
                    }
                    pLevel.playSound(null, pPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    ((ServerLevel) pLevel).sendParticles(ParticleTypes.HAPPY_VILLAGER, pPos.getX() + 0.5, pPos.getY() + 1,
                            pPos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.05);
                    return InteractionResult.SUCCESS;
                }
            } else {
                pLevel.playSound(pPlayer, pPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                pLevel.addParticle(ParticleTypes.HAPPY_VILLAGER, pPos.getX() + 0.5, pPos.getY() + 1.0,
                        pPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public boolean canGrow (Level pLevel, BlockPos pPos) {
        BlockState blockBelow = pLevel.getBlockState(pPos.below());
        return blockBelow.is(ModBlocks.INFUSED_DIRT.get());
    }
}
