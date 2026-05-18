package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.astralya.hexalia.util.ModUtil;
import net.astralya.hexalia.util.SidedItemHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CenserBlockEntity extends SyncBlockEntity implements Clearable {

    private static final int SIZE = 2;
    private static final int SLOT_0 = 0;
    private static final int SLOT_1 = 1;

    private static final int EFFECT_INTERVAL = 40;

    private final ItemStackHandler inventory;

    private final IItemHandler upInputHandler;
    private final IItemHandler lockedHandler;
    private final IItemHandler blockedHandler;

    private HerbCombination activeCombination;
    private int burnTime;
    private boolean effectActive;

    public CenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENSER.get(), pos, state);

        this.inventory = new ItemStackHandler(SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    inventoryChanged();
                    sendUpdate();
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };

        this.upInputHandler = SidedItemHandlers.view(inventory, new int[]{SLOT_0, SLOT_1}, true, false);
        this.lockedHandler = SidedItemHandlers.view(inventory, new int[]{SLOT_0, SLOT_1}, false, false);
        this.blockedHandler = SidedItemHandlers.view(inventory, new int[]{}, false, false);

        this.activeCombination = null;
        this.burnTime = 0;
        this.effectActive = false;
    }

    @SuppressWarnings("unused")
    public IItemHandler getItemHandler(@Nullable Direction side) {
        BlockState st = getBlockState();
        boolean lit = st.hasProperty(CenserBlock.LIT) && st.getValue(CenserBlock.LIT);

        if (lit) {
            return lockedHandler;
        }

        if (side == Direction.UP) {
            return upInputHandler;
        }

        return blockedHandler;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && activeCombination != null && burnTime > 0 && !effectActive) {
            CenserEffectHandler.registerActiveEffect(level, pos, activeCombination, burnTime);
            effectActive = true;
        }
        if (!state.getValue(CenserBlock.LIT)) return;
        if (burnTime > 0) {
            burnTime--;
            if (burnTime <= 0) {
                extinguish(level, pos, state);
            }
            setChanged();
        }
    }

    private void extinguish(Level level, BlockPos pos, BlockState state) {
        if (activeCombination != null) {
            CenserEffectHandler.removeActiveEffect(level, pos);
            activeCombination = null;
            effectActive = false;
        }
        level.setBlockAndUpdate(pos, state.setValue(CenserBlock.LIT, false));
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.0f);
        inventoryChanged();
    }

    public void reactivateEffect() {
        if (activeCombination != null && burnTime > 0 && !effectActive && level != null) {
            CenserEffectHandler.registerActiveEffect(level, worldPosition, activeCombination, burnTime);
            effectActive = true;
        }
    }

    public void setBurnTime(int time) {
        this.burnTime = time;
        setChanged();
        sendUpdate();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        return inventory.getStackInSlot(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) return;
        ItemStack one = stack.copy();
        one.setCount(1);
        inventory.setStackInSlot(slot, one);
    }

    public void clearItems() {
        inventory.setStackInSlot(SLOT_0, ItemStack.EMPTY);
        inventory.setStackInSlot(SLOT_1, ItemStack.EMPTY);
        setChanged();
        sendUpdate();
    }

    public SimpleContainer getDropsContainer() {
        SimpleContainer container = new SimpleContainer(SIZE);
        for (int i = 0; i < SIZE; i++) {
            container.setItem(i, inventory.getStackInSlot(i));
        }
        return container;
    }

    private void sendUpdate() {
        if (level != null && !level.isClientSide()) {
            Packet<ClientGamePacketListener> updatePacket = getUpdatePacket();
            for (ServerPlayer player : ModUtil.tracking((ServerLevel) level, worldPosition)) {
                player.connection.send(updatePacket);
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setActiveCombination(HerbCombination combo) {
        this.activeCombination = combo;
        inventoryChanged();
    }

    public HerbCombination getActiveCombination() {
        return this.activeCombination;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("Items"));

        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getCount() <= 0 || stack.getCount() > stack.getMaxStackSize()) {
                stack.setCount(1);
            }
            if (stack.getCount() > 1) {
                stack.setCount(1);
            }
            inventory.setStackInSlot(i, stack);
        }

        if (tag.contains("ActiveCombination")) {
            CompoundTag comboTag = tag.getCompound("ActiveCombination");
            Item item1 = BuiltInRegistries.ITEM.byId(comboTag.getInt("Item1"));
            Item item2 = BuiltInRegistries.ITEM.byId(comboTag.getInt("Item2"));
            this.activeCombination = new HerbCombination(item1, item2);
        } else {
            this.activeCombination = null;
        }

        burnTime = tag.getInt("BurnTime");
        effectActive = false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Items", inventory.serializeNBT(registries));
        tag.putInt("BurnTime", burnTime);
        tag.putBoolean("EffectActive", effectActive);

        if (activeCombination != null) {
            CompoundTag comboTag = new CompoundTag();
            comboTag.putInt("Item1", BuiltInRegistries.ITEM.getId(activeCombination.item1()));
            comboTag.putInt("Item2", BuiltInRegistries.ITEM.getId(activeCombination.item2()));
            tag.put("ActiveCombination", comboTag);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide && activeCombination != null && burnTime > 0) {
            reactivateEffect();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isEmpty() {
        return inventory.getStackInSlot(SLOT_0).isEmpty() && inventory.getStackInSlot(SLOT_1).isEmpty();
    }

    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        ItemStack stack = inventory.getStackInSlot(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        inventory.setStackInSlot(slot, ItemStack.EMPTY);
        setChanged();
        sendUpdate();
        return stack;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}