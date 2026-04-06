package net.astralya.hexalia.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BriarSickleItem extends ShearsItem {

    public static final float ATTACK_DAMAGE_BONUS = 1.0F;
    public static final float ATTACK_SPEED = -2.0F;

    public BriarSickleItem(Settings settings) {
        super(settings);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.LEAVES)) {
            return 15.0F;
        }
        if (state.isIn(BlockTags.FLOWERS) || state.isIn(BlockTags.REPLACEABLE_BY_TREES)) {
            return 10.0F;
        }
        return super.getMiningSpeed(stack, state);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS;
        }

        ItemStack stack = context.getStack();
        BlockPos center = context.getBlockPos();
        boolean clearedAny = false;
        boolean shearingMode = player.isSneaking();

        for (BlockPos pos : BlockPos.iterate(center.add(-1, 0, -1), center.add(1, 0, 1))) {
            BlockState state = world.getBlockState(pos);
            if (!isClearablePlant(state)) {
                continue;
            }

            if (shearingMode) {
                if (state.isOf(Blocks.TALL_GRASS) || state.isOf(Blocks.LARGE_FERN)) {
                    BlockPos basePos = state.contains(Properties.DOUBLE_BLOCK_HALF)
                            && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                            ? pos.down()
                            : pos;

                    BlockState baseState = world.getBlockState(basePos);
                    if (baseState.isOf(state.getBlock())) {
                        Block.dropStack(world, basePos, new ItemStack(baseState.getBlock().asItem()));
                        world.removeBlock(basePos, false);
                        world.removeBlock(basePos.up(), false);
                        serverWorld.syncWorldEvent(null, 2001, basePos, Block.getRawIdFromState(baseState));
                        serverWorld.syncWorldEvent(null, 2001, basePos.up(), Block.getRawIdFromState(world.getBlockState(basePos.up())));
                        clearedAny = true;
                    }
                } else {
                    ItemStack drop = new ItemStack(state.getBlock().asItem());
                    if (!drop.isEmpty()) {
                        Block.dropStack(world, pos, drop);
                    }
                    world.removeBlock(pos, false);
                    serverWorld.syncWorldEvent(null, 2001, pos, Block.getRawIdFromState(state));
                    clearedAny = true;
                }
            } else {
                if (world.breakBlock(pos, true, player)) {
                    clearedAny = true;
                }
            }

            if (stack.isEmpty()) {
                break;
            }
        }

        if (!clearedAny) {
            return ActionResult.PASS;
        }

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                0.8F,
                1.0F
        );
        player.swingHand(context.getHand(), true);
        stack.damage(1, player, player.getPreferredEquipmentSlot(stack));
        return ActionResult.CONSUME;
    }

    private static boolean isClearablePlant(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || state.isIn(BlockTags.FLOWERS)
                || state.isIn(BlockTags.SAPLINGS)
                || state.isIn(BlockTags.LEAVES);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}