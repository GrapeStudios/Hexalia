package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.custom.CenserBlock;
import net.astralya.hexalia.block.custom.censer.CenserEffectHandler;
import net.astralya.hexalia.block.custom.censer.HerbCombination;
import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CenserBlockEntity extends SyncBlockEntity {

    private static final int SIZE = 2;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    private HerbCombination activeCombination = null;
    private int burnTime = 0;
    private static final int EFFECT_INTERVAL = 40;
    private boolean effectActive = false;

    public CenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENSER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CenserBlockEntity be) {
        if (!state.get(CenserBlock.LIT)) return;

        if (be.burnTime > 0) {
            be.burnTime--;

            if (be.burnTime % EFFECT_INTERVAL == 0 && be.activeCombination != null) {
                CenserEffectHandler.applyEffects(world, pos, be.activeCombination);

                if (!be.effectActive) {
                    CenserEffectHandler.registerActiveEffect(world, pos, be.activeCombination, be.burnTime);
                    be.effectActive = true;
                }
            }

            if (be.burnTime <= 0) {
                be.extinguish(world, pos, state);
            }

            be.inventoryChanged();
        }
    }

    private void extinguish(World world, BlockPos pos, BlockState state) {
        if (activeCombination != null) {
            CenserEffectHandler.clearPlayerEffectsInRange(world, pos);
            CenserEffectHandler.removeActiveEffect(pos);
            activeCombination = null;
            effectActive = false;
        }

        world.setBlockState(pos, state.with(CenserBlock.LIT, false), Block.NOTIFY_ALL);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 1.0f);
        inventoryChanged();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        return items.get(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) return;
        items.set(slot, stack);
        inventoryChanged();
    }

    public void clearInventory() {
        for (int i = 0; i < SIZE; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        inventoryChanged();
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void setActiveCombination(HerbCombination combo) {
        this.activeCombination = combo;
        inventoryChanged();
    }

    public HerbCombination getActiveCombination() {
        return this.activeCombination;
    }

    public void setBurnTime(int time) {
        this.burnTime = time;
        inventoryChanged();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);

        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;
            if (stack.getCount() <= 0 || stack.getCount() > stack.getMaxCount()) {
                stack.setCount(1);
            }
        }

        if (nbt.contains("ActiveCombination")) {
            NbtCompound comboTag = nbt.getCompound("ActiveCombination");
            Item item1 = Item.byRawId(comboTag.getInt("Item1"));
            Item item2 = Item.byRawId(comboTag.getInt("Item2"));
            this.activeCombination = new HerbCombination(item1, item2);
        }

        burnTime = nbt.getInt("BurnTime");
        effectActive = nbt.getBoolean("EffectActive");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt("BurnTime", burnTime);
        nbt.putBoolean("EffectActive", effectActive);

        if (activeCombination != null) {
            NbtCompound comboTag = new NbtCompound();
            comboTag.putInt("Item1", Item.getRawId(activeCombination.item1()));
            comboTag.putInt("Item2", Item.getRawId(activeCombination.item2()));
            nbt.put("ActiveCombination", comboTag);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= SIZE) return ItemStack.EMPTY;
        ItemStack stack = items.get(slot).copy();
        items.set(slot, ItemStack.EMPTY);
        inventoryChanged();
        return stack;
    }
}
