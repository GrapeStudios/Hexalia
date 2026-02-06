package net.astralya.hexalia.util;

import net.astralya.hexalia.block.custom.MorphoraBlock;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.MutavisItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ModVanillaBehaviors {

    private ModVanillaBehaviors() {
    }

    public static void register() {
        registerDispenserBehaviors();
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.MUTAVIS.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();

                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);
                BlockState targetState = level.getBlockState(targetPos);

                if (stack.getItem() instanceof MutavisItem mutavisItem) {
                    if (mutavisItem.tryMutate(level, targetPos, stack, null)) {
                        return stack;
                    }
                }

                if (targetState.getBlock() instanceof MorphoraBlock morphoraBlock) {
                    if (morphoraBlock.tryActivateWithMutavis(level, targetPos, stack, null)) {
                        return stack;
                    }
                }

                return super.execute(source, stack);
            }
        });
    }
}
