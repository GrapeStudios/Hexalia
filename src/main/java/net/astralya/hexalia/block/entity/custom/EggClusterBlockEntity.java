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
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class EggClusterBlockEntity extends BlockEntity {

    private static final String TAG_HATCH_TICKS = "HatchTicksRemaining";
    private int hatchTicksRemaining;

    public EggClusterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.EGG_CLUSTER, pos, state);
        this.hatchTicksRemaining = getHatchDurationTicks();
    }

    public static void tick(World world, BlockPos pos, BlockState state, EggClusterBlockEntity blockEntity) {
        if (world.isClient) return;
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
        BlockEntity belowBe = world.getBlockEntity(belowPos);
        if (belowBe != null) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, belowPos, world.getBlockState(belowPos), belowBe, Direction.UP);
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
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            tx.commit();
            if (inserted >= stack.getCount()) return ItemStack.EMPTY;
            ItemStack remaining = stack.copy();
            remaining.setCount((int) (stack.getCount() - inserted));
            return remaining;
        }
    }

    private static int getHatchDurationTicks() {
        return Configuration.EGG_CLUSTER_HATCH_DURATION.get();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt(TAG_HATCH_TICKS, this.hatchTicksRemaining);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains(TAG_HATCH_TICKS)) {
            this.hatchTicksRemaining = nbt.getInt(TAG_HATCH_TICKS);
        } else {
            this.hatchTicksRemaining = getHatchDurationTicks();
        }
    }
}