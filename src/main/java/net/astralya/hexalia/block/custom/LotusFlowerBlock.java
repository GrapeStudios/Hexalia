package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LotusFlowerBlock extends LilyPadBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LotusFlowerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                          PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ItemStack held = player.getStackInHand(hand);

        if (held.isOf(ModItems.ATHAME)) {
            if (!world.isClient) {
                Block.dropStack(world, pos, new ItemStack(ModItems.LOTUS_BLOSSOM));

                BlockState target = Blocks.LILY_PAD.getDefaultState();
                if (target.contains(FACING) && state.contains(FACING)) {
                    target = target.with(FACING, state.get(FACING));
                }
                world.setBlockState(pos, target, Block.NOTIFY_ALL);

                EquipmentSlot slot = LivingEntity.getSlotForHand(hand);
                held.damage(1, player, slot);

                world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (world instanceof ServerWorld server) {
                    server.spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            8, 0.25, 0.15, 0.25, 0.04
                    );
                }
            } else {
                world.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        0.0, 0.0, 0.0
                );
            }
            return ItemActionResult.SUCCESS;
        }

        if (!held.isOf(Items.BONE_MEAL)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!world.isClient) {
            Block.dropStack(world, pos, new ItemStack(this));
            if (!player.getAbilities().creativeMode) {
                held.decrement(1);
            }
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (world instanceof ServerWorld server) {
                server.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        10, 0.25, 0.25, 0.25, 0.05
                );
            }
            return ItemActionResult.SUCCESS;
        } else {
            world.playSound(player, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    0.0, 0.0, 0.0
            );
            return ItemActionResult.SUCCESS;
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!this.canPlaceAt(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos())) {
            return null;
        }
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }
}
