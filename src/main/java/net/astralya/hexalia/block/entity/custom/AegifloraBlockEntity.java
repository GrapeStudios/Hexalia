package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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
        if (charges <= 0) return AbsorbOutcome.NONE;
        charges--;
        BlockState current = getCachedState();
        if (charges == 1) {
            if (current.isOf(ModBlocks.AEGIFLORA)) {
                world.setBlockState(pos, ModBlocks.WITHERED_AEGIFLORA.getDefaultState(), 3);
            } else {
                markDirty();
                world.updateListeners(pos, current, current, 3);
            }
            return AbsorbOutcome.WITHERED;
        }
        if (charges <= 0) {
            world.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState(), 3);
            return AbsorbOutcome.DESTROYED;
        }
        markDirty();
        world.updateListeners(pos, current, current, 3);
        return AbsorbOutcome.NONE;
    }

    private static int defaultChargesForState(BlockState state) {
        if (state.isOf(ModBlocks.WITHERED_AEGIFLORA)) return 1;
        return 2;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt(TAG_CHARGES, charges);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains(TAG_CHARGES)) {
            charges = nbt.getInt(TAG_CHARGES);
        } else {
            charges = defaultChargesForState(getCachedState());
        }
    }
}