package net.astralya.hexalia.block.entity.custom;
import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
public class CenserBlockEntity extends BlockEntity implements SidedInventory {

    private static final int SIZE = 2;
    private static final int SLOT_0 = 0;
    private static final int SLOT_1 = 1;
    private static final int[] TOP_SLOTS = new int[]{SLOT_0, SLOT_1};
    private static final int[] NO_SLOTS = new int[0];
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    private @Nullable HerbCombination activeCombination;
    private int burnTime;
    private boolean effectActive;

    public CenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENSER, pos, state);
    }
    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient && activeCombination != null && burnTime > 0 && !effectActive) {
            CenserEffectHandler.registerActiveEffect(world, pos, activeCombination, burnTime);
            effectActive = true;
        }
        if (!state.get(CenserBlock.LIT)) {
            return;
        }
        if (burnTime > 0) {
            burnTime--;
            if (burnTime <= 0) {
                extinguish(world, pos, state);
            }
            markDirty();
        }
    }
    private void extinguish(World world, BlockPos pos, BlockState state) {
        if (activeCombination != null) {
            CenserEffectHandler.removeActiveEffect(world, pos);
            activeCombination = null;
            effectActive = false;
        }
        world.setBlockState(pos, state.with(CenserBlock.LIT, false));
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5f, 1.0f);
        onInventoryChanged();
    }
    public void reactivateEffect() {
        if (activeCombination != null && burnTime > 0 && !effectActive && world != null) {
            CenserEffectHandler.registerActiveEffect(world, pos, activeCombination, burnTime);
            effectActive = true;
        }
    }
    public void setBurnTime(int time) {
        burnTime = time;
        markDirty();
        sendUpdate();
    }
    public int getBurnTime() {
        return burnTime;
    }
    public void clearItems() {
        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);
        onInventoryChanged();
    }
    public DefaultedList<ItemStack> getDropsContainer() {
        return inventory;
    }
    private void sendUpdate() {
        if (world != null && !world.isClient) {
            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    private void onInventoryChanged() {
        markDirty();
        sendUpdate();
    }
    public void setActiveCombination(@Nullable HerbCombination combo) {
        activeCombination = combo;
        onInventoryChanged();
    }
    public @Nullable HerbCombination getActiveCombination() {
        return activeCombination;
    }
    public void onChunkLoad() {
        if (world != null && !world.isClient && activeCombination != null && burnTime > 0) {
            reactivateEffect();
        }
    }
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);
        if (nbt.contains("Items")) {
            NbtCompound itemsTag = nbt.getCompound("Items");
            for (int i = 0; i < SIZE; i++) {
                String key = "Slot" + i;
                if (!itemsTag.contains(key)) {
                    continue;
                }
                NbtCompound stackTag = itemsTag.getCompound(key);
                if (stackTag.isEmpty()) {
                    continue;
                }
                ItemStack stack = ItemStack.fromNbt(registries, stackTag).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    stack.setCount(1);
                }
                inventory.set(i, stack);
            }
        }
        if (nbt.contains("ActiveCombination")) {
            NbtCompound comboTag = nbt.getCompound("ActiveCombination");
            if (comboTag.contains("Item1") && comboTag.contains("Item2")) {
                Item item1 = Registries.ITEM.get(Identifier.of(comboTag.getString("Item1")));
                Item item2 = Registries.ITEM.get(Identifier.of(comboTag.getString("Item2")));
                activeCombination = new HerbCombination(item1, item2);
            } else {
                activeCombination = null;
            }
        } else {
            activeCombination = null;
        }
        burnTime = nbt.getInt("BurnTime");
        effectActive = false;
    }
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        NbtCompound itemsTag = new NbtCompound();
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            itemsTag.put("Slot" + i, stack.encode(registries));
        }
        nbt.put("Items", itemsTag);
        nbt.putInt("BurnTime", burnTime);
        nbt.putBoolean("EffectActive", effectActive);
        if (activeCombination != null) {
            NbtCompound comboTag = new NbtCompound();
            comboTag.putString("Item1", Registries.ITEM.getId(activeCombination.item1()).toString());
            comboTag.putString("Item2", Registries.ITEM.getId(activeCombination.item2()).toString());
            nbt.put("ActiveCombination", comboTag);
        }
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registries);
        return nbt;
    }
    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (getCachedState().get(CenserBlock.LIT)) {
            return NO_SLOTS;
        }
        return side == Direction.UP ? TOP_SLOTS : NO_SLOTS;
    }
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction direction) {
        if (getCachedState().get(CenserBlock.LIT)) {
            return false;
        }
        return direction == Direction.UP
                && slot >= 0
                && slot < SIZE
                && !stack.isEmpty()
                && inventory.get(slot).isEmpty();
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return false;
    }
    @Override
    public int size() {
        return SIZE;
    }
    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
    @Override
    public boolean isEmpty() {
        return inventory.get(SLOT_0).isEmpty() && inventory.get(SLOT_1).isEmpty();
    }
    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }
        return inventory.get(slot);
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(inventory, slot, amount);
        if (!removed.isEmpty()) {
            onInventoryChanged();
        }
        return removed;
    }
    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = stack.copy();
        inventory.set(slot, ItemStack.EMPTY);
        onInventoryChanged();
        return removed;
    }
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return;
        }
        if (!stack.isEmpty() && !inventory.get(slot).isEmpty()) {
            return;
        }
        ItemStack one = stack.copy();
        if (!one.isEmpty()) {
            one.setCount(1);
        }
        inventory.set(slot, one);
        onInventoryChanged();
    }
    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (getCachedState().get(CenserBlock.LIT)) {
            return false;
        }
        return slot >= 0
                && slot < SIZE
                && !stack.isEmpty()
                && inventory.get(slot).isEmpty();
    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null
                && world.getBlockEntity(pos) == this
                && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
    @Override
    public void clear() {
        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);
        onInventoryChanged();
    }
}
