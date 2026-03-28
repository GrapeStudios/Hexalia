package net.astralya.hexalia.event;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.RootshaperItem;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RootshaperEventHandler {

    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (BREAKING.get()) return true;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return true;
            if (!serverPlayer.isSneaking()) return true;

            ItemStack stack = serverPlayer.getMainHandStack();
            if (!(stack.getItem() instanceof RootshaperItem)) return true;

            Direction face = getPlayerFacing(serverPlayer);

            BREAKING.set(true);
            try {
                for (BlockPos adjacent : get3x3Positions(pos, face)) {
                    if (adjacent.equals(pos)) continue;

                    BlockState adjacentState = world.getBlockState(adjacent);
                    if (adjacentState.isAir() || adjacentState.getHardness(world, adjacent) < 0) continue;
                    if (!canRootshaperBreak(adjacentState)) continue;

                    boolean broke = serverPlayer.interactionManager.tryBreakBlock(adjacent);
                    if (broke && !serverPlayer.isCreative()) {
                        stack.damage(1, serverPlayer, serverPlayer.getPreferredEquipmentSlot(stack));
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
            } finally {
                BREAKING.set(false);
            }

            return true;
        });
    }

    private static boolean canRootshaperBreak(BlockState state) {
        return state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.SHOVEL_MINEABLE);
    }

    public static void onLeftClickBlock(ServerPlayerEntity player, BlockPos pos, World world) {
        ItemStack stack = player.getMainHandStack();
        if (!stack.isOf(ModItems.ROOTSHAPER)) return;
        BlockState state = world.getBlockState(pos);
        int newMode = RootshaperItem.computeMode(state);
        if (newMode != RootshaperItem.getMode(stack)) {
            RootshaperItem.setMode(stack, newMode);
        }
    }

    private static Direction getPlayerFacing(ServerPlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0F);
        double ax = Math.abs(look.x);
        double ay = Math.abs(look.y);
        double az = Math.abs(look.z);
        if (ay > ax && ay > az) {
            return look.y > 0 ? Direction.UP : Direction.DOWN;
        } else if (ax > az) {
            return look.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return look.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static List<BlockPos> get3x3Positions(BlockPos center, Direction face) {
        List<BlockPos> positions = new ArrayList<>();
        Direction[] axes = getPerpendicularAxes(face);
        Direction a = axes[0];
        Direction b = axes[1];
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                positions.add(center.offset(a, i).offset(b, j));
            }
        }
        return positions;
    }

    private static Direction[] getPerpendicularAxes(Direction face) {
        return switch (face) {
            case UP, DOWN     -> new Direction[]{ Direction.NORTH, Direction.EAST };
            case NORTH, SOUTH -> new Direction[]{ Direction.EAST,  Direction.UP   };
            case EAST, WEST   -> new Direction[]{ Direction.NORTH, Direction.UP   };
        };
    }
}