package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class AegifloraBlockEntity extends BlockEntity {
    public enum AbsorbOutcome {
        NONE,
        WITHERED,
        DESTROYED
    }

    private static final String TAG_CHARGES = "Charges";

    private int charges;

    public AegifloraBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.AEGIFLORA, pos, state);
        this.charges = defaultChargesForState(state);
    }

    public boolean canAbsorb() {
        return charges > 0;
    }

    public int getChargesRemaining() {
        return charges;
    }

    public AbsorbOutcome absorbOnce(ServerWorld world) {
        if (charges <= 0) {
            return AbsorbOutcome.NONE;
        }

        charges--;
        BlockState current = getCachedState();

        if (charges == 1) {
            if (current.isOf(ModBlocks.AEGIFLORA)) {
                world.setBlockState(pos, ModBlocks.WITHERED_AEGIFLORA.getDefaultState(), Block.NOTIFY_ALL);
            } else {
                markDirty();
                world.updateListeners(pos, current, current, Block.NOTIFY_ALL);
            }
            return AbsorbOutcome.WITHERED;
        }

        if (charges <= 0) {
            world.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), Block.NOTIFY_ALL);
            return AbsorbOutcome.DESTROYED;
        }

        markDirty();
        world.updateListeners(pos, current, current, Block.NOTIFY_ALL);
        return AbsorbOutcome.NONE;
    }

    private static int defaultChargesForState(BlockState state) {
        return state.isOf(ModBlocks.WITHERED_AEGIFLORA) ? 1 : 2;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt(TAG_CHARGES, charges);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(TAG_CHARGES)) {
            charges = nbt.getInt(TAG_CHARGES);
        } else {
            charges = defaultChargesForState(getCachedState());
        }
    }
}