package net.astralya.hexalia.entity.boat;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ModChestBoatEntity extends ChestBoat {
  private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
      SynchedEntityData.defineId(ModChestBoatEntity.class, EntityDataSerializers.INT);

  public ModChestBoatEntity(EntityType<? extends ChestBoat> entityType, Level level) {
    super(entityType, level);
  }

  public ModChestBoatEntity(Level level, double x, double y, double z) {
    this(ModEntities.MOD_CHEST_BOAT.get(), level);
    setPos(x, y, z);
    xo = x;
    yo = y;
    zo = z;
  }

  @Override
  public Item getDropItem() {
    return switch (getModVariant()) {
      case WILLOW -> ModItems.WILLOW_CHEST_BOAT.get();
      case COTTONWOOD -> ModItems.COTTONWOOD_CHEST_BOAT.get();
    };
  }

  public void setVariant(ModBoatEntity.Type variant) {
    entityData.set(DATA_ID_TYPE, variant.ordinal());
  }

  public ModBoatEntity.Type getModVariant() {
    return ModBoatEntity.Type.byId(entityData.get(DATA_ID_TYPE));
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.Builder builder) {
    super.defineSynchedData(builder);
    builder.define(DATA_ID_TYPE, ModBoatEntity.Type.WILLOW.ordinal());
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    compound.putString("Type", getModVariant().getSerializedName());
    addChestVehicleSaveData(compound, registryAccess());
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    readChestVehicleSaveData(compound, registryAccess());
    if (compound.contains("Type", 8)) {
      setVariant(ModBoatEntity.Type.byName(compound.getString("Type")));
    }
  }
}
