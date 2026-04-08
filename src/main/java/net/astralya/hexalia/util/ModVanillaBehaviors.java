package net.astralya.hexalia.util;

import com.mojang.authlib.GameProfile;
import net.astralya.hexalia.block.custom.MorphoraBlock;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.MutavisItem;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class ModVanillaBehaviors {

    private static final GameProfile DISPENSER_PROFILE = new GameProfile(UUID.fromString("5b10f7e7-0b45-4db5-9c64-ce0f1a7f2d63"), "[HexaliaDispenser]");

    private ModVanillaBehaviors() {
    }

    public static void register() {
        registerDispenserBehaviors();
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.MUTAVIS, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.getWorld();
                Direction facing = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos targetPos = pointer.getPos().offset(facing);

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
    }

    private static void registerUseOnBehavior(Item item) {
        DispenserBlock.registerBehavior(item, new FallibleItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.getWorld();
                Direction facing = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos dispenserPos = pointer.getPos();
                BlockPos targetPos = dispenserPos.offset(facing);

                FakePlayer fakePlayer = FakePlayer.get(world, DISPENSER_PROFILE);
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

                ActionResult result = world.getBlockState(targetPos).onUse(world, fakePlayer, Hand.MAIN_HAND, hit);

                if (!result.isAccepted()) {
                    result = fakePlayer.getStackInHand(Hand.MAIN_HAND).useOnBlock(new ItemUsageContext(fakePlayer, Hand.MAIN_HAND, hit));
                }

                this.setSuccess(result.isAccepted());
                return fakePlayer.getStackInHand(Hand.MAIN_HAND);
            }
        });
    }
}