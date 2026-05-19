package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AegifloraBlockEntity extends BlockEntity {
  private static final String TAG_CHARGES = "Charges";
  private int charges;

  public AegifloraBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntityTypes.AEGIFLORA.get(), pos, state);
    charges = defaultChargesForState(state);
  }

  public boolean canAbsorb() {
    return charges > 0;
  }

  public AbsorbOutcome absorbOnce(ServerLevel level) {
    if (charges <= 0) {
      return AbsorbOutcome.NONE;
    }
    charges--;
    BlockState current = getBlockState();
    if (charges == 1) {
      if (current.is(ModBlocks.AEGIFLORA.get())) {
        level.setBlock(worldPosition, ModBlocks.WITHERED_AEGIFLORA.get().defaultBlockState(), 3);
      } else {
        setChanged();
        level.sendBlockUpdated(worldPosition, current, current, 3);
      }
      return AbsorbOutcome.WITHERED;
    }
    if (charges <= 0) {
      level.setBlock(worldPosition, Blocks.DEAD_BUSH.defaultBlockState(), 3);
      return AbsorbOutcome.DESTROYED;
    }
    setChanged();
    level.sendBlockUpdated(worldPosition, current, current, 3);
    return AbsorbOutcome.NONE;
  }

  private static int defaultChargesForState(BlockState state) {
    return state.is(ModBlocks.WITHERED_AEGIFLORA.get()) ? 1 : 2;
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt(TAG_CHARGES, charges);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    charges =
        tag.contains(TAG_CHARGES)
            ? tag.getInt(TAG_CHARGES)
            : defaultChargesForState(getBlockState());
  }

  public enum AbsorbOutcome {
    NONE,
    WITHERED,
    DESTROYED
  }
}
