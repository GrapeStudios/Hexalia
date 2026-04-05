package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.particle.ModParticleType;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.HoeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class InfusedDirtBlock extends Block {

    public InfusedDirtBlock(Settings settings) {
        super(settings);
    }

    public static void init() {
        TillableBlockRegistry.register(
                ModBlocks.INFUSED_DIRT,
                HoeItem::canTillFarmland,
                ModBlocks.INFUSED_FARMLAND.getDefaultState()
        );
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        spawnBubbleParticles(world, pos);
    }

    private void spawnBubbleParticles(World world, BlockPos pos) {
        if (world.isClient) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; i++) {
                double x = pos.getX() + 0.5 + random.nextDouble(-0.5, 0.5);
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5 + random.nextDouble(-0.5, 0.5);
                world.addParticle(ModParticleType.INFUSED_BUBBLE, x, y, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }
}
