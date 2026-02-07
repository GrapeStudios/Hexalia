package net.astralya.hexalia.worlddata.fireproof;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;

public final class WaxedBlocksSavedData extends SavedData {

    private static final String DATA_NAME = "hexalia_fireproof_wax";

    private static final Factory<WaxedBlocksSavedData> FACTORY = new Factory<>(
            WaxedBlocksSavedData::new,
            WaxedBlocksSavedData::load
    );

    private final Long2ObjectOpenHashMap<BlockState> originalStateByPos = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<LongSet> posByChunk = new Long2ObjectOpenHashMap<>();

    public static WaxedBlocksSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public boolean isWaxed(BlockPos pos) {
        return originalStateByPos.containsKey(pos.asLong());
    }

    public BlockState getOriginalState(BlockPos pos) {
        BlockState state = originalStateByPos.get(pos.asLong());
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    }

    public LongSet getPositionsForChunkKey(long chunkKey) {
        LongSet set = posByChunk.get(chunkKey);
        return set == null ? LongOpenHashSet.of() : set;
    }

    public void wax(BlockPos pos, BlockState original) {
        long posKey = pos.asLong();
        if (originalStateByPos.containsKey(posKey)) {
            return;
        }
        originalStateByPos.put(posKey, original);

        long chunkKey = new ChunkPos(pos).toLong();
        LongSet set = posByChunk.get(chunkKey);
        if (set == null) {
            set = new LongOpenHashSet();
            posByChunk.put(chunkKey, set);
        }
        set.add(posKey);

        setDirty();
    }

    public void unwax(BlockPos pos) {
        long posKey = pos.asLong();
        BlockState removed = originalStateByPos.remove(posKey);
        if (removed == null) {
            return;
        }

        long chunkKey = new ChunkPos(pos).toLong();
        LongSet set = posByChunk.get(chunkKey);
        if (set != null) {
            set.remove(posKey);
            if (set.isEmpty()) {
                posByChunk.remove(chunkKey);
            }
        }

        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();

        for (var entry : originalStateByPos.long2ObjectEntrySet()) {
            long posKey = entry.getLongKey();
            BlockState state = entry.getValue();

            CompoundTag e = new CompoundTag();
            e.putLong("pos", posKey);
            writeState(e, state);
            list.add(e);
        }

        tag.put("waxed", list);
        return tag;
    }

    public static WaxedBlocksSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        WaxedBlocksSavedData data = new WaxedBlocksSavedData();
        ListTag list = tag.getList("waxed", 10);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            long posKey = e.getLong("pos");
            BlockState state = readState(e);

            data.originalStateByPos.put(posKey, state);

            BlockPos pos = BlockPos.of(posKey);
            long chunkKey = new ChunkPos(pos).toLong();

            LongSet set = data.posByChunk.get(chunkKey);
            if (set == null) {
                set = new LongOpenHashSet();
                data.posByChunk.put(chunkKey, set);
            }
            set.add(posKey);
        }

        return data;
    }

    private static void writeState(CompoundTag out, BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        out.putString("block", id.toString());

        CompoundTag props = new CompoundTag();
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            Property<?> property = entry.getKey();
            Comparable<?> value = entry.getValue();
            props.putString(property.getName(), value.toString());
        }
        out.put("props", props);
    }

    private static BlockState readState(CompoundTag in) {
        ResourceLocation id = ResourceLocation.tryParse(in.getString("block"));
        if (id == null) {
            return Blocks.AIR.defaultBlockState();
        }

        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            return Blocks.AIR.defaultBlockState();
        }

        BlockState state = BuiltInRegistries.BLOCK.get(id).defaultBlockState();
        CompoundTag props = in.getCompound("props");

        for (String key : props.getAllKeys()) {
            String value = props.getString(key);
            state = applyProperty(state, key, value);
        }

        return state;
    }

    private static BlockState applyProperty(BlockState state, String name, String value) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
        if (property == null) {
            return state;
        }
        return setParsed(state, property, value);
    }

    private static <T extends Comparable<T>> BlockState setParsed(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}