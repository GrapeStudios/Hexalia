package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LotusFlowerBlock extends WaterlilyBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public LotusFlowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(ModItems.ATHAME.get())) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(ModItems.LOTUS_BLOSSOM.get()));

                BlockState target = Blocks.LILY_PAD.defaultBlockState();
                if (target.hasProperty(FACING) && state.hasProperty(FACING)) {
                    target = target.setValue(FACING, state.getValue(FACING));
                }
                level.setBlock(pos, target, Block.UPDATE_ALL);

                if (!player.isCreative() && held.isDamageableItem() && level instanceof ServerLevel server && player instanceof ServerPlayer sp) {
                    held.hurtAndBreak(1, server, sp, broken -> player.onEquippedItemBroken(broken, LivingEntity.getSlotForHand(hand)));
                    if (held.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }

                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (level instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            8, 0.25, 0.15, 0.25, 0.04);
                }
            } else {
                level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        0.0, 0.0, 0.0);
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (held.is(Items.BONE_MEAL)) {
            if (!level.isClientSide()) {
                popResource(level, pos, new ItemStack(this));
                if (!player.isCreative()) {
                    held.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                        10, 0.25, 0.25, 0.25, 0.05);
                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(player, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        0.0, 0.0, 0.0);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos())) {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}