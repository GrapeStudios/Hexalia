package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.gameplay.censer.CenserEffectHandler;
import net.astralya.hexalia.gameplay.censer.HerbCombination;
import net.astralya.hexalia.util.ModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CenserBlockEntity extends BlockEntity {

    private static final int SIZE   = 2;
    private static final int SLOT_0 = 0;
    private static final int SLOT_1 = 1;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    private @Nullable HerbCombination activeCombination;
    private int burnTime;
    private boolean effectActive;

    public CenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENSER, pos, state);
        this.activeCombination = null;
        this.burnTime = 0;
        this.effectActive = false;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient && activeCombination != null && burnTime > 0 && !effectActive) {
            CenserEffectHandler.registerActiveEffect(world, pos, activeCombination, burnTime);
            effectActive = true;
        }
        if (!state.get(CenserBlock.LIT)) return;
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
        this.burnTime = time;
        markDirty();
        sendUpdate();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        return inventory.get(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) return;
        ItemStack one = stack.copy();
        one.setCount(1);
        inventory.set(slot, one);
        onInventoryChanged();
    }

    public void clearItems() {
        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);
        markDirty();
        sendUpdate();
    }

    public DefaultedList<ItemStack> getDropsContainer() {
        return inventory;
    }

    private void sendUpdate() {
        if (world != null && !world.isClient) {
            for (ServerPlayerEntity player : ModUtil.tracking((ServerWorld) world, pos)) {
                player.networkHandler.sendPacket(toUpdatePacket());
            }
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private void onInventoryChanged() {
        markDirty();
        sendUpdate();
    }

    public void setActiveCombination(@Nullable HerbCombination combo) {
        this.activeCombination = combo;
        onInventoryChanged();
    }

    public @Nullable HerbCombination getActiveCombination() {
        return this.activeCombination;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);

        if (nbt.contains("Items", NbtCompound.COMPOUND_TYPE)) {
            NbtCompound itemsTag = nbt.getCompound("Items");
            for (int i = 0; i < SIZE; i++) {
                String key = "Slot" + i;
                if (!itemsTag.contains(key, NbtCompound.COMPOUND_TYPE)) continue;

                NbtCompound stackTag = itemsTag.getCompound(key);
                if (stackTag.isEmpty()) continue;

                ItemStack stack = ItemStack.fromNbt(registries, stackTag).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    stack.setCount(1);
                }
                inventory.set(i, stack);
            }
        }

        if (nbt.contains("ActiveCombination", NbtCompound.COMPOUND_TYPE)) {
            NbtCompound comboTag = nbt.getCompound("ActiveCombination");
            if (comboTag.contains("Item1") && comboTag.contains("Item2")) {
                Item item1 = Registries.ITEM.get(Identifier.of(comboTag.getString("Item1")));
                Item item2 = Registries.ITEM.get(Identifier.of(comboTag.getString("Item2")));
                this.activeCombination = new HerbCombination(item1, item2);
            } else {
                this.activeCombination = null;
            }
        } else {
            this.activeCombination = null;
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
            if (stack.isEmpty()) continue;
            itemsTag.put("Slot" + i, (NbtCompound) stack.encode(registries));
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

    public void onChunkLoad() {
        if (world != null && !world.isClient && activeCombination != null && burnTime > 0) {
            reactivateEffect();
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public boolean isEmpty() {
        return inventory.get(SLOT_0).isEmpty() && inventory.get(SLOT_1).isEmpty();
    }

    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        sendUpdate();
        return stack;
    }
}