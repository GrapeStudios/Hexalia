package net.astralya.hexalia.item.data;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public final class SpiritrootTetherData {

    private static final String TAG_KEY = "SpiritrootTether";
    private static final String KEY_DIMENSION = "Dimension";
    private static final String KEY_X = "X";
    private static final String KEY_Y = "Y";
    private static final String KEY_Z = "Z";
    private static final String KEY_MOB_TYPE = "MobType";
    private static final String KEY_MOB_NBT = "MobNbt";
    private static final String KEY_MOB_UUID = "MobUuid";
    private static final String KEY_MOB_NAME = "MobName";

    public static final SpiritrootTetherData EMPTY =
            new SpiritrootTetherData("", 0, 0, 0, "", "", "", "");

    private final String boundDimension;
    private final int boundX;
    private final int boundY;
    private final int boundZ;
    private final String mobType;
    private final String mobNbt;
    private final String mobUuid;
    private final String mobNameJson;

    public SpiritrootTetherData(String boundDimension, int boundX, int boundY, int boundZ,
                                String mobType, String mobNbt, String mobUuid, String mobNameJson) {
        this.boundDimension = boundDimension;
        this.boundX = boundX;
        this.boundY = boundY;
        this.boundZ = boundZ;
        this.mobType = mobType;
        this.mobNbt = mobNbt;
        this.mobUuid = mobUuid;
        this.mobNameJson = mobNameJson;
    }

    public String boundDimension() {
        return this.boundDimension;
    }

    public int boundX() {
        return this.boundX;
    }

    public int boundY() {
        return this.boundY;
    }

    public int boundZ() {
        return this.boundZ;
    }

    public String mobType() {
        return this.mobType;
    }

    public String mobNbt() {
        return this.mobNbt;
    }

    public String mobUuid() {
        return this.mobUuid;
    }

    public String mobNameJson() {
        return this.mobNameJson;
    }

    public boolean hasMob() {
        return !this.mobType.isEmpty();
    }

    public boolean hasBound() {
        return !this.boundDimension.isEmpty();
    }

    public Optional<BoundLocation> bound() {
        if (this.boundDimension.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new BoundLocation(this.boundDimension, new BlockPos(this.boundX, this.boundY, this.boundZ)));
    }

    public Optional<CapturedMob> mob() {
        if (this.mobType.isEmpty()) {
            return Optional.empty();
        }

        Identifier typeId = Identifier.tryParse(this.mobType);
        if (typeId == null) {
            return Optional.empty();
        }

        return Optional.of(new CapturedMob(
                typeId,
                this.mobNbt,
                parseUuid(this.mobUuid).orElse(null),
                this.mobNameJson
        ));
    }

    public SpiritrootTetherData withBound(RegistryKey<World> dimension, BlockPos pos) {
        Identifier id = dimension.getValue();
        return new SpiritrootTetherData(
                id.toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                this.mobType,
                this.mobNbt,
                this.mobUuid,
                this.mobNameJson
        );
    }

    public SpiritrootTetherData withCapturedMob(Identifier typeId, NbtCompound nbt, UUID uuid, String nameJson) {
        String snbt = nbt.toString();
        String uuidStr = uuid != null ? uuid.toString() : "";
        String name = nameJson != null ? nameJson : "";
        return new SpiritrootTetherData(
                this.boundDimension,
                this.boundX,
                this.boundY,
                this.boundZ,
                typeId.toString(),
                snbt,
                uuidStr,
                name
        );
    }

    public SpiritrootTetherData clearedMob() {
        return new SpiritrootTetherData(
                this.boundDimension,
                this.boundX,
                this.boundY,
                this.boundZ,
                "",
                "",
                "",
                ""
        );
    }

    public void save(ItemStack stack) {
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound tag = new NbtCompound();
        tag.putString(KEY_DIMENSION, this.boundDimension);
        tag.putInt(KEY_X, this.boundX);
        tag.putInt(KEY_Y, this.boundY);
        tag.putInt(KEY_Z, this.boundZ);
        tag.putString(KEY_MOB_TYPE, this.mobType);
        tag.putString(KEY_MOB_NBT, this.mobNbt);
        tag.putString(KEY_MOB_UUID, this.mobUuid);
        tag.putString(KEY_MOB_NAME, this.mobNameJson);
        root.put(TAG_KEY, tag);
    }

    public static SpiritrootTetherData load(ItemStack stack) {
        NbtCompound root = stack.getNbt();
        if (root == null || !root.contains(TAG_KEY)) {
            return EMPTY;
        }

        NbtCompound tag = root.getCompound(TAG_KEY);
        return new SpiritrootTetherData(
                tag.getString(KEY_DIMENSION),
                tag.getInt(KEY_X),
                tag.getInt(KEY_Y),
                tag.getInt(KEY_Z),
                tag.getString(KEY_MOB_TYPE),
                tag.getString(KEY_MOB_NBT),
                tag.getString(KEY_MOB_UUID),
                tag.getString(KEY_MOB_NAME)
        );
    }

    public record BoundLocation(String dimensionId, BlockPos pos) {
        public RegistryKey<World> dimension() {
            Identifier id = Identifier.tryParse(this.dimensionId);
            if (id == null) {
                id = World.OVERWORLD.getValue();
            }
            return RegistryKey.of(RegistryKeys.WORLD, id);
        }
    }

    public record CapturedMob(Identifier typeId, String snbt, UUID uuid, String nameJson) {
    }

    private static Optional<UUID> parseUuid(String value) {
        if (value == null || value.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}