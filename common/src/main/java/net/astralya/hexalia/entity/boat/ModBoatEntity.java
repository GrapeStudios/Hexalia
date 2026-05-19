package net.astralya.hexalia.entity.boat;

import java.util.function.Function;
import java.util.function.IntFunction;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ModBoatEntity extends Boat {
  private static final EntityDataAccessor<Integer> DATA_ID_TYPE =
      SynchedEntityData.defineId(ModBoatEntity.class, EntityDataSerializers.INT);

  public ModBoatEntity(EntityType<? extends Boat> entityType, Level level) {
    super(entityType, level);
  }

  public ModBoatEntity(Level level, double x, double y, double z) {
    this(ModEntities.MOD_BOAT.get(), level);
    setPos(x, y, z);
    xo = x;
    yo = y;
    zo = z;
  }

  @Override
  public Item getDropItem() {
    return switch (getModVariant()) {
      case WILLOW -> ModItems.WILLOW_BOAT.get();
      case COTTONWOOD -> ModItems.COTTONWOOD_BOAT.get();
    };
  }

  public void setVariant(Type variant) {
    entityData.set(DATA_ID_TYPE, variant.ordinal());
  }

  public Type getModVariant() {
    return Type.byId(entityData.get(DATA_ID_TYPE));
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.Builder builder) {
    super.defineSynchedData(builder);
    builder.define(DATA_ID_TYPE, Type.WILLOW.ordinal());
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    compound.putString("Type", getModVariant().getSerializedName());
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    if (compound.contains("Type", 8)) {
      setVariant(Type.byName(compound.getString("Type")));
    }
  }

  public enum Type implements StringRepresentable {
    WILLOW(ModBlocks.WILLOW_PLANKS.get(), "willow"),
    COTTONWOOD(ModBlocks.COTTONWOOD_PLANKS.get(), "cottonwood");

    private static final Function<String, Type> BY_NAME =
        StringRepresentable.createNameLookup(values(), Function.identity());
    private static final IntFunction<Type> BY_ID =
        ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final Block planks;
    private final String name;

    Type(Block planks, String name) {
      this.planks = planks;
      this.name = name;
    }

    @Override
    public String getSerializedName() {
      return name;
    }

    public String getName() {
      return name;
    }

    public Block getPlanks() {
      return planks;
    }

    @Override
    public String toString() {
      return name;
    }

    public static Type byId(int id) {
      return BY_ID.apply(id);
    }

    public static Type byName(String name) {
      Type type = BY_NAME.apply(name);
      return type == null ? WILLOW : type;
    }
  }
}
