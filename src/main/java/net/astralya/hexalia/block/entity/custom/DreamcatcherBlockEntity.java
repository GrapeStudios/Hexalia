package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DreamcatcherBlockEntity extends BlockEntity {

    private static final int TICKS_PER_NODE = 30000;
    private int fuelTicks = 0;

    public DreamcatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DREAMCATCHER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, DreamcatcherBlockEntity be) {
        if (world.isClient) return;
        if (!world.isNight()) return;
        if (!be.hasFuel()) return;
        be.fuelTicks--;
        if (be.fuelTicks < 0) be.fuelTicks = 0;
        be.markDirty();
    }

    public boolean hasFuel() {
        return fuelTicks > 0;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public ActionResult tryInsertFuel(PlayerEntity player, ItemStack held) {
        if (!held.isOf(ModItems.FIRE_NODE)) return ActionResult.PASS;
        if (fuelTicks > 0) return ActionResult.FAIL;
        if (!player.isCreative()) held.decrement(1);
        fuelTicks = TICKS_PER_NODE;
        markDirty();
        syncToClient();
        return ActionResult.SUCCESS;
    }

    public ItemStack tryExtractFuel(PlayerEntity player) {
        if (fuelTicks <= 0) return ItemStack.EMPTY;
        fuelTicks = 0;
        markDirty();
        syncToClient();
        return new ItemStack(ModItems.FIRE_NODE);
    }

    public void spawnActiveParticles(World world, BlockPos pos, Random random) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        for (int i = 0; i < 2; i++) {
            double x = cx + (random.nextDouble() - 0.5) * 0.4;
            double y = cy + random.nextDouble() * 0.3;
            double z = cz + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0.02, 0);
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.01, 0);
        }
    }

    private void syncToClient() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("FuelTicks", fuelTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        fuelTicks = nbt.getInt("FuelTicks");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        nbt.putInt("FuelTicks", fuelTicks);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}