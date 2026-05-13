package net.astralya.hexalia.util;

import com.mojang.authlib.GameProfile;
import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.custom.MorphoraBlock;
import net.astralya.hexalia.block.custom.SmallCauldronBlock;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.MutavisItem;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.UUID;

public final class ModVanillaBehaviors {

    private ModVanillaBehaviors() {
    }

    public static void register() {
        registerMutavisBlockUse();
        registerFireStarterBlockUse();
        registerDispenserBehaviors();
    }

    private static void registerMutavisBlockUse() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!(stack.getItem() instanceof MutavisItem mutavisItem)) {
                return ActionResult.PASS;
            }
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            BlockPos pos = hit.getBlockPos();
            ServerWorld serverWorld = (ServerWorld) world;
            if (mutavisItem.tryMutate(serverWorld, pos, stack, player)) {
                return ActionResult.CONSUME;
            }
            if (world.getBlockState(pos).getBlock() instanceof MorphoraBlock morphoraBlock
                    && morphoraBlock.tryActivateWithMutavis(serverWorld, pos, stack, player)) {
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
    }

    private static void registerFireStarterBlockUse() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!isFireStarter(stack)) {
                return ActionResult.PASS;
            }
            BlockState state = world.getBlockState(hit.getBlockPos());
            if (!(state.getBlock() instanceof CenserBlock || state.getBlock() instanceof SmallCauldronBlock)) {
                return ActionResult.PASS;
            }
            return tryIgniteBlock(state, world, hit.getBlockPos(), player, hand, hit).toActionResult();
        });
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.MUTAVIS, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.world();
                Direction facing = pointer.state().get(DispenserBlock.FACING);
                BlockPos targetPos = pointer.pos().offset(facing);
                if (stack.getItem() instanceof MutavisItem mutavisItem && mutavisItem.tryMutate(world, targetPos, stack, null)) {
                    return stack;
                }
                if (world.getBlockState(targetPos).getBlock() instanceof MorphoraBlock morphoraBlock && morphoraBlock.tryActivateWithMutavis(world, targetPos, stack, null)) {
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
        registerUseOnBehavior(ModItems.CELESTIAL_CRYSTAL);
        registerUseOnBehavior(ModItems.HEX_FOCUS);
        registerUseOnBehavior(ModItems.LOTUS_BLOSSOM);
        registerFireStarterDispenserBehavior(Items.FLINT_AND_STEEL);
        registerFireStarterDispenserBehavior(Items.FIRE_CHARGE);
    }

    private static void registerUseOnBehavior(Item item) {
        DispenserBlock.registerBehavior(item, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.world();
                Direction facing = pointer.state().get(DispenserBlock.FACING);
                BlockPos dispenserPos = pointer.pos();
                BlockPos targetPos = dispenserPos.offset(facing);
                FakePlayer fakePlayer = FakePlayer.get(world, new GameProfile(
                        UUID.fromString("5b10f7e7-0b45-4db5-9c64-ce0f1a7f2d63"),
                        "[HexaliaDispenser]"
                ));
                fakePlayer.refreshPositionAndAngles(
                        dispenserPos.getX() + 0.5D,
                        dispenserPos.getY() + 0.5D,
                        dispenserPos.getZ() + 0.5D,
                        facing.asRotation(),
                        0.0F
                );
                fakePlayer.setStackInHand(Hand.MAIN_HAND, stack);
                Direction hitFace = facing.getOpposite();
                Vec3d hitLocation = Vec3d.ofCenter(targetPos).add(
                        hitFace.getOffsetX() * 0.5D,
                        hitFace.getOffsetY() * 0.5D,
                        hitFace.getOffsetZ() * 0.5D
                );
                BlockHitResult hit = new BlockHitResult(hitLocation, hitFace, targetPos, false);
                ActionResult result = fakePlayer.getStackInHand(Hand.MAIN_HAND).useOnBlock(new ItemUsageContext(fakePlayer, Hand.MAIN_HAND, hit));
                this.setSuccess(result.isAccepted());
                return fakePlayer.getStackInHand(Hand.MAIN_HAND);
            }
        });
    }

    private static void registerFireStarterDispenserBehavior(Item item) {
        DispenserBlock.registerBehavior(item, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.world();
                Direction facing = pointer.state().get(DispenserBlock.FACING);
                BlockPos targetPos = pointer.pos().offset(facing);
                BlockState targetState = world.getBlockState(targetPos);
                boolean success = false;
                if (targetState.getBlock() instanceof CenserBlock censerBlock) {
                    ItemActionResult result = censerBlock.tryIgniteWithFireStarter(stack, targetState, world, targetPos, null);
                    success = result == ItemActionResult.CONSUME;
                } else if (targetState.getBlock() instanceof SmallCauldronBlock smallCauldronBlock) {
                    ItemActionResult result = smallCauldronBlock.tryIgniteWithFireStarter(stack, targetState, world, targetPos, null, Hand.MAIN_HAND);
                    success = result == ItemActionResult.CONSUME;
                }
                if (success) {
                    if (stack.isOf(Items.FIRE_CHARGE)) {
                        stack.decrement(1);
                    } else {
                        stack.damage(1, world, null, i -> {});
                        if (stack.isEmpty()) {
                            world.syncWorldEvent(1010, pointer.pos(), 0);
                            this.setSuccess(true);
                            return ItemStack.EMPTY;
                        }
                    }
                    this.setSuccess(true);
                } else {
                    this.setSuccess(false);
                }
                return stack;
            }
        });
    }

    private static ItemActionResult tryIgniteBlock(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (state.getBlock() instanceof CenserBlock censerBlock) {
            return censerBlock.tryIgniteWithFireStarter(stack, state, world, pos, player);
        }
        if (state.getBlock() instanceof SmallCauldronBlock smallCauldronBlock) {
            ItemActionResult result = smallCauldronBlock.tryIgniteWithFireStarter(stack, state, world, pos, player, hand);
            return result == null ? ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : result;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean isFireStarter(ItemStack stack) {
        return stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE);
    }
}
