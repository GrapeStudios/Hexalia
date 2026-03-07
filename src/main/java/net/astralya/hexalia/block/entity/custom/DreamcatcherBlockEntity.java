package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DreamcatcherBlockEntity extends SyncBlockEntity {

    private static final int TICKS_PER_NODE = 30000;

    private int fuelTicks = 0;

    public DreamcatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DREAMCATCHER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DreamcatcherBlockEntity be) {
        if (level.isClientSide) return;
        if (!level.isNight()) return;
        if (!be.hasFuel()) return;

        be.fuelTicks--;
        if (be.fuelTicks < 0) be.fuelTicks = 0;
        be.setChanged();
    }

    public boolean hasFuel() {
        return fuelTicks > 0;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public InteractionResult tryInsertFuel(Player player, ItemStack held) {
        if (!held.is(ModItems.FIRE_NODE.get())) return InteractionResult.PASS;
        if (fuelTicks > 0) return InteractionResult.FAIL;
        if (!player.isCreative()) held.shrink(1);
        fuelTicks = TICKS_PER_NODE;
        setChanged();
        sendUpdate();
        return InteractionResult.SUCCESS;
    }

    public ItemStack tryExtractFuel(Player player) {
        if (fuelTicks <= 0) return ItemStack.EMPTY;
        fuelTicks = 0;
        setChanged();
        sendUpdate();
        return new ItemStack(ModItems.FIRE_NODE.get());
    }

    private void sendUpdate() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("FuelTicks", fuelTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuelTicks = tag.getInt("FuelTicks");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}