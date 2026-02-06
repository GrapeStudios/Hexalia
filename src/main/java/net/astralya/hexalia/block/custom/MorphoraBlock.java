package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.util.MutationOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class MorphoraBlock extends EnchantedPlantBlock {

    public MorphoraBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.MAGMA_BLOCK) || state.isSolid();
    }

    public boolean tryActivateWithMutavis(ServerLevel level, BlockPos morphoraPos, ItemStack mutavisStack, Player player) {
        boolean anyConverted = false;

        int radius = Configuration.MORPHORA_RADIUS.get();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos targetPos = morphoraPos.offset(dx, 0, dz);
                if (targetPos.equals(morphoraPos)) {
                    continue;
                }

                BlockState targetState = level.getBlockState(targetPos);
                if (targetState.isAir()) {
                    continue;
                }

                ItemStack inputStack = targetState.getBlock().asItem().getDefaultInstance();
                if (inputStack.isEmpty()) {
                    continue;
                }

                Optional<MutationRecipe> match = level.getRecipeManager()
                        .getRecipeFor(ModRecipes.MUTATION_TYPE.get(), new MutationRecipeInput(inputStack), level)
                        .map(RecipeHolder::value);

                if (match.isEmpty()) {
                    continue;
                }

                ItemStack result = match.get().assemble(new MutationRecipeInput(inputStack), level.registryAccess());
                level.destroyBlock(targetPos, false);

                MutationOutput.apply(level, targetPos, result);

                emitEffects(level, targetPos);
                anyConverted = true;
            }
        }

        if (!anyConverted) {
            return false;
        }

        if (player == null || !player.getAbilities().instabuild) {
            mutavisStack.shrink(1);
        }

        return true;
    }

    private void emitEffects(ServerLevel server, BlockPos pos) {
        server.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        server.sendParticles(
                ModParticleType.LEAVES.get(),
                pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }
}