package net.astralya.hexalia.item.custom;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;

public class RootshaperItem extends Item {

    private static final Map<BlockState, BlockState> PATH_STATES = ImmutableMap.<BlockState, BlockState>builder()
            .put(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .put(Blocks.DIRT.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .put(Blocks.PODZOL.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .put(Blocks.COARSE_DIRT.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .put(Blocks.MYCELIUM.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .put(Blocks.ROOTED_DIRT.getDefaultState(), Blocks.DIRT_PATH.getDefaultState())
            .build();

    public static final int MODE_PICKAXE = 0;
    public static final int MODE_SHOVEL = 1;
    public static final float MINING_SPEED = 9.0F;
    public static final int DAMAGE_PER_BLOCK = 1;
    public static final float ATTACK_DAMAGE_BONUS = 4.0F;
    public static final float ATTACK_SPEED = -2.8F;

    public RootshaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (context.getSide() == Direction.DOWN) {
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();
        BlockState result = null;

        BlockState flattened = PATH_STATES.get(state);
        if (flattened != null && world.getBlockState(pos.up()).isAir()) {
            result = flattened;
        }

        if (result == null && state.isOf(Blocks.CAMPFIRE) && state.get(CampfireBlock.LIT)) {
            result = state.with(CampfireBlock.LIT, false);
        }

        if (result == null) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            ItemStack stack = context.getStack();
            int oldMode = getMode(stack);
            if (oldMode != MODE_SHOVEL) {
                setMode(stack, MODE_SHOVEL);
                playMorphSound(world, player);
            }
            world.setBlockState(pos, result, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, result));
            if (player != null) {
                stack.damage(1, player, player.getPreferredEquipmentSlot(stack));
            }
        }

        return world.isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean sneaking = MinecraftClient.getInstance().player != null
                && MinecraftClient.getInstance().player.isSneaking();
        if (sneaking) {
            tooltip.add(Text.translatable("tooltip.hexalia.rootshaper.mode_3x3_active").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.hexalia.rootshaper.mode_3x3_hint").formatted(Formatting.GRAY));
        }
    }

    public static int computeMode(BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) return MODE_PICKAXE;
        if (state.isIn(BlockTags.SHOVEL_MINEABLE)) return MODE_SHOVEL;
        return MODE_PICKAXE;
    }

    public static int getMode(ItemStack stack) {
        CustomModelDataComponent cmd = stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT);
        return cmd.value();
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(mode));
    }

    public static void playMorphSound(World world, PlayerEntity player) {
        world.playSound(
                null,
                player != null ? player.getBlockPos() : BlockPos.ORIGIN,
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.PLAYERS,
                0.6F,
                1.2F
        );
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            if (state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE)) {
                stack.damage(DAMAGE_PER_BLOCK, miner, miner.getPreferredEquipmentSlot(stack));
            }
        }
        return true;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE)) {
            return MINING_SPEED;
        }
        return super.getMiningSpeed(stack, state);
    }
}