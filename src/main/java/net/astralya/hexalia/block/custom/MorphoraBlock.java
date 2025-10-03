package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;

public class MorphoraBlock extends EnchantedPlantBlock {

    public MorphoraBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.MAGMA_BLOCK) || floor.isSolidBlock(world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty() || stack.getItem() != ModItems.MUTAVIS) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.CONSUME;
        }

        ServerWorld server = (ServerWorld) world;
        boolean anyConverted = false;

        int radius = Configuration.common().plants.morphoraEffectRadius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos targetPos = pos.add(dx, 0, dz);
                if (targetPos.equals(pos)) continue;

                BlockState targetState = server.getBlockState(targetPos);
                if (targetState.isAir()) continue;

                ItemStack inputStack = new ItemStack(targetState.getBlock().asItem());
                if (inputStack.isEmpty()) continue;

                SimpleInventory inv = new SimpleInventory(inputStack);
                Optional<MutationRecipe> match = world.getRecipeManager()
                        .getFirstMatch(ModRecipes.MUTATION_TYPE, inv, world);

                if (match.isPresent()) {
                    ItemStack result = match.get().craft(inv, server.getRegistryManager());
                    server.breakBlock(targetPos, false);
                    if (!result.isEmpty()) {
                        server.spawnEntity(new ItemEntity(
                                server,
                                targetPos.getX() + 0.5,
                                targetPos.getY() + 0.25,
                                targetPos.getZ() + 0.5,
                                result
                        ));
                    }
                    emitEffects(server, targetPos);
                    anyConverted = true;
                }
            }
        }

        if (anyConverted) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    private void emitEffects(ServerWorld server, BlockPos pos) {
        server.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.5F, 1.0F);
        server.spawnParticles(
                ModParticleType.LEAVES,
                pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }
}