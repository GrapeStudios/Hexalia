package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class EggClusterBlockEntity extends BlockEntity {
    private static final String TAG_HATCH_TICKS = "HatchTicksRemaining";

    private int hatchTicksRemaining;

    public EggClusterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.EGG_CLUSTER, pos, state);
        hatchTicksRemaining = getHatchDurationTicks();
    }

    public static void tick(World world, BlockPos pos, BlockState state, EggClusterBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }

        if (blockEntity.hatchTicksRemaining > 0) {
            blockEntity.hatchTicksRemaining--;
            blockEntity.markDirty();
            return;
        }

        blockEntity.hatch(world, pos);
    }

    private void hatch(World world, BlockPos pos) {
        ItemStack stack = new ItemStack(ModItems.SILKWORM, 4);
        BlockPos belowPos = pos.down();
        BlockEntity belowBlockEntity = world.getBlockEntity(belowPos);

        if (belowBlockEntity != null) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, belowPos, world.getBlockState(belowPos), belowBlockEntity, Direction.UP);
            if (storage != null) {
                stack = insertAll(storage, stack);
            }
        }

        if (!stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    pos.getX() + 0.5,
                    pos.getY() + 0.25,
                    pos.getZ() + 0.5,
                    stack
            );
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }

        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    private ItemStack insertAll(Storage<ItemVariant> storage, ItemStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
            transaction.commit();

            if (inserted >= stack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack remaining = stack.copy();
            remaining.setCount((int) (stack.getCount() - inserted));
            return remaining;
        }
    }

    private static int getHatchDurationTicks() {
        return Configuration.EGG_CLUSTER_HATCH_DURATION.get();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(TAG_HATCH_TICKS, hatchTicksRemaining);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(TAG_HATCH_TICKS)) {
            hatchTicksRemaining = nbt.getInt(TAG_HATCH_TICKS);
        } else {
            hatchTicksRemaining = getHatchDurationTicks();
        }
    }
}