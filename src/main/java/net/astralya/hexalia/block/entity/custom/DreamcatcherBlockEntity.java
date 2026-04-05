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

    public static void tick(World world, BlockPos pos, BlockState state, DreamcatcherBlockEntity blockEntity) {
        if (world.isClient || !world.isNight() || !blockEntity.hasFuel()) {
            return;
        }

        blockEntity.fuelTicks--;
        if (blockEntity.fuelTicks < 0) {
            blockEntity.fuelTicks = 0;
        }

        blockEntity.markDirty();
        blockEntity.syncToClient();
    }

    public boolean hasFuel() {
        return fuelTicks > 0;
    }

    public int getFuelTicks() {
        return fuelTicks;
    }

    public ActionResult tryInsertFuel(PlayerEntity player, ItemStack heldStack) {
        if (!heldStack.isOf(ModItems.FIRE_NODE)) {
            return ActionResult.PASS;
        }

        if (fuelTicks > 0) {
            return ActionResult.FAIL;
        }

        if (!player.isCreative()) {
            heldStack.decrement(1);
        }

        fuelTicks = TICKS_PER_NODE;
        markDirty();
        syncToClient();
        return ActionResult.SUCCESS;
    }

    public ItemStack tryExtractFuel(PlayerEntity player) {
        if (fuelTicks <= 0) {
            return ItemStack.EMPTY;
        }

        fuelTicks = 0;
        markDirty();
        syncToClient();
        return new ItemStack(ModItems.FIRE_NODE);
    }

    public void spawnActiveParticles(World world, BlockPos pos, Random random) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 2; i++) {
            double x = centerX + (random.nextDouble() - 0.5) * 0.4;
            double y = centerY + random.nextDouble() * 0.3;
            double z = centerZ + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.02, 0.0);
            world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.01, 0.0);
        }
    }

    private void syncToClient() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("FuelTicks", fuelTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fuelTicks = nbt.getInt("FuelTicks");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}