package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.util.MutationOutput;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Optional;

public class MorphoraBlock extends EnchantedPlantBlock {

    public MorphoraBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.MAGMA_BLOCK) || floor.isSolidBlock(world, pos);
    }

    public boolean tryActivateWithMutavis(ServerWorld world, BlockPos morphoraPos, ItemStack mutavisStack, PlayerEntity player) {
        boolean anyConverted = false;
        int radius = Configuration.MORPHORA_RADIUS.get();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos targetPos = morphoraPos.add(dx, 0, dz);
                if (targetPos.equals(morphoraPos)) continue;
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.isAir()) continue;
                ItemStack inputStack = targetState.getBlock().asItem().getDefaultStack();
                if (inputStack.isEmpty()) continue;
                Optional<MutationRecipe> match = world.getRecipeManager()
                        .getFirstMatch(ModRecipes.MUTATION_TYPE, new MutationRecipeInput(inputStack), world)
                        .map(RecipeEntry::value);
                if (match.isEmpty()) continue;
                ItemStack result = match.get().craft(new MutationRecipeInput(inputStack), world.getRegistryManager());
                world.breakBlock(targetPos, false);
                MutationOutput.apply(world, targetPos, result);
                emitEffects(world, targetPos);
                anyConverted = true;
            }
        }
        if (!anyConverted) {
            return false;
        }
        if (player == null || !player.getAbilities().creativeMode) {
            mutavisStack.decrement(1);
        }
        return true;
    }

    private void emitEffects(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.5F, 1.0F);
        world.spawnParticles(
                ModParticleType.LEAVES,
                pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }
}