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
    private static final int SIZE = 2;
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

    public static void tick(World world, BlockPos pos, BlockState state, CenserBlockEntity blockEntity) {
        if (!world.isClient && blockEntity.activeCombination != null && blockEntity.burnTime > 0 && !blockEntity.effectActive) {
            CenserEffectHandler.registerActiveEffect(world, pos, blockEntity.activeCombination, blockEntity.burnTime);
            blockEntity.effectActive = true;
        }

        if (!state.get(CenserBlock.LIT)) {
            return;
        }

        if (blockEntity.burnTime > 0) {
            blockEntity.burnTime--;
            if (blockEntity.burnTime <= 0) {
                blockEntity.extinguish(world, pos, state);
            }
            blockEntity.markDirty();
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
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }
        return inventory.get(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return;
        }

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
            Packet<ClientPlayPacketListener> packet = toUpdatePacket();
            if (packet != null) {
                for (ServerPlayerEntity player : ModUtil.tracking((ServerWorld) world, pos)) {
                    player.networkHandler.sendPacket(packet);
                }
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
        return activeCombination;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.set(SLOT_0, ItemStack.EMPTY);
        inventory.set(SLOT_1, ItemStack.EMPTY);

        if (nbt.contains("Items", 10)) {
            NbtCompound itemsTag = nbt.getCompound("Items");
            for (int i = 0; i < SIZE; i++) {
                String key = "Slot" + i;
                if (!itemsTag.contains(key, 10)) {
                    continue;
                }

                NbtCompound stackTag = itemsTag.getCompound(key);
                if (stackTag.isEmpty()) {
                    continue;
                }

                ItemStack stack = ItemStack.fromNbt(stackTag);
                if (!stack.isEmpty()) {
                    stack.setCount(1);
                }
                inventory.set(i, stack);
            }
        }

        if (nbt.contains("ActiveCombination", 10)) {
            NbtCompound comboTag = nbt.getCompound("ActiveCombination");
            if (comboTag.contains("Item1", 8) && comboTag.contains("Item2", 8)) {
                Item item1 = Registries.ITEM.get(new Identifier(comboTag.getString("Item1")));
                Item item2 = Registries.ITEM.get(new Identifier(comboTag.getString("Item2")));
                this.activeCombination = new HerbCombination(item1, item2);
            } else {
                this.activeCombination = null;
            }
        } else {
            this.activeCombination = null;
        }

        this.burnTime = nbt.getInt("BurnTime");
        this.effectActive = false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtCompound itemsTag = new NbtCompound();
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            itemsTag.put("Slot" + i, stack.writeNbt(new NbtCompound()));
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
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public boolean isEmpty() {
        return inventory.get(SLOT_0).isEmpty() && inventory.get(SLOT_1).isEmpty();
    }

    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        sendUpdate();
        return stack;
    }
}