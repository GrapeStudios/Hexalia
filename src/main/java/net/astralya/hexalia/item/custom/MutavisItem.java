package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class MutavisItem extends Item {

    public MutavisItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        var state = level.getBlockState(pos);

        ItemStack inputStack = new ItemStack(state.getBlock().asItem());
        if (inputStack.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            Optional<MutationRecipe> match = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.MUTATION_TYPE.get(), new MutationRecipeInput(inputStack), level)
                    .map(RecipeHolder::value);

            if (match.isPresent()) {
                level.destroyBlock(pos, false);

                ServerLevel server = (ServerLevel) level;
                ItemStack out = match.get().assemble(new MutationRecipeInput(inputStack), server.registryAccess());
                if (!out.isEmpty()) {
                    server.addFreshEntity(new ItemEntity(
                            server,
                            pos.getX() + 0.5,
                            pos.getY() + 0.25,
                            pos.getZ() + 0.5,
                            out.copy()
                    ));
                }

                Player player = context.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }

                emitEffects(server, pos);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
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
