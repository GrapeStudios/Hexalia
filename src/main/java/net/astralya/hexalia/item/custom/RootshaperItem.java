package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.sound.ModSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class RootshaperItem extends Item {

    public static final int MODE_PICKAXE    = 0;
    public static final int MODE_SHOVEL     = 1;
    public static final float MINING_SPEED  = 9.0F;
    public static final int DAMAGE_PER_BLOCK = 1;
    public static final float ATTACK_DAMAGE_BONUS = 4.0F;
    public static final float ATTACK_SPEED  = -2.8F;

    public RootshaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        BlockState result = state.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
        if (result != null && !level.getBlockState(pos.above()).isAir()) {
            result = null;
        }
        if (result == null) {
            result = state.getToolModifiedState(context, ItemAbilities.SHOVEL_DOUSE, false);
        }
        if (result == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            ItemStack stack = context.getItemInHand();
            int oldMode = getMode(stack);
            if (oldMode != MODE_SHOVEL) {
                setMode(stack, MODE_SHOVEL);
                playMorphSound(level, player);
            }
            level.setBlock(pos, result, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, result));
            if (player != null) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        boolean sneaking = Minecraft.getInstance().player != null && Minecraft.getInstance().player.isShiftKeyDown();
        if (sneaking) {
            tooltipComponents.add(Component.translatable("tooltip.hexalia.rootshaper.mode_3x3_active").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.hexalia.rootshaper.mode_3x3_hint").withStyle(ChatFormatting.GRAY));
        }
    }

    public static Tool createTool() {
        return new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, MINING_SPEED),
                        Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, MINING_SPEED),
                        Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                ),
                1.0F,
                DAMAGE_PER_BLOCK
        );
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ATTACK_DAMAGE_BONUS, Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, ATTACK_SPEED, Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public static int computeMode(BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return MODE_PICKAXE;
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) return MODE_SHOVEL;
        return MODE_PICKAXE;
    }

    public static int getMode(ItemStack stack) {
        CustomModelData cmd = stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT);
        return cmd.value();
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(mode));
    }

    public static void playMorphSound(Level level, Player player) {
        level.playSound(
                null,
                player != null ? player.blockPosition() : BlockPos.ZERO,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                0.6F,
                1.2F
        );
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ability == ItemAbilities.PICKAXE_DIG
                || ability == ItemAbilities.SHOVEL_DIG
                || ability == ItemAbilities.SHOVEL_FLATTEN
                || ability == ItemAbilities.SHOVEL_DOUSE;
    }
}