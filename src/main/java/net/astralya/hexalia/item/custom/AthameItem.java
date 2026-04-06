package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class AthameItem extends Item {

    private static final Set<Block> STRIPPABLE_LOGS = Set.of(Blocks.DARK_OAK_LOG, ModBlocks.COTTONWOOD_LOG.get());
    private static final Map<Block, Block> STRIPPED_BLOCKS = Map.of(
            Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG,
            ModBlocks.COTTONWOOD_LOG.get(), ModBlocks.STRIPPED_COTTONWOOD_LOG.get()
    );

    public AthameItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && state.is(BlockTags.PLANKS)) {
            transformPlanksIntoBrazier(level, blockPos, player, context);
            return InteractionResult.SUCCESS;
        }

        if (STRIPPABLE_LOGS.contains(state.getBlock())) {
            stripLog(level, blockPos, state, player, context);
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    private void transformPlanksIntoBrazier(Level level, BlockPos blockPos, Player player, UseOnContext context) {
        if (!level.isClientSide) {
            level.playSound(null, blockPos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);

            Block.popResource(level, blockPos, new ItemStack(ModBlocks.RITUAL_BRAZIER.get()));

            handleItemDamage(player, context.getItemInHand(), context);
        }
    }

    private void stripLog(Level level, BlockPos blockPos, BlockState state, Player player, UseOnContext context) {
        if (!level.isClientSide) {
            level.playSound(null, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);

            Block strippedBlock = getStrippedBlock(state.getBlock());
            level.setBlock(blockPos, strippedBlock.defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 3);

            Block.popResource(level, blockPos, new ItemStack(ModItems.TREE_RESIN.get()));

            handleItemDamage(player, context.getItemInHand(), context);
        }
    }

    private Block getStrippedBlock(Block originalBlock) {
        return STRIPPED_BLOCKS.getOrDefault(originalBlock, originalBlock);
    }

    private void handleItemDamage(Player player, ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!player.isCreative() && stack.isDamageableItem()) {
            context.getItemInHand().hurtAndBreak(1, ((ServerLevel) level), ((ServerPlayer) context.getPlayer()),
                    item -> Objects.requireNonNull(context.getPlayer()).onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
            if (stack.isEmpty()) {
                player.setItemInHand(context.getHand(), ItemStack.EMPTY);
            }
        }
    }
}
