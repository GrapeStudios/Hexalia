package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.Configuration;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class MorphoraBlock extends EnchantedPlantBlock {

    public MorphoraBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.MAGMA_BLOCK) || state.isSolidRender(level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || stack.getItem() != ModItems.MUTAVIS.get()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel server = (ServerLevel) level;
        boolean anyConverted = false;

        int radius = Configuration.MORPHORA_RADIUS.get();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos targetPos = pos.offset(dx, 0, dz);
                if (targetPos.equals(pos)) continue;

                BlockState targetState = server.getBlockState(targetPos);
                if (targetState.isAir()) continue;

                ItemStack inputStack = targetState.getBlock().asItem().getDefaultInstance();
                if (inputStack.isEmpty()) continue;

                Optional<MutationRecipe> match = server.getRecipeManager()
                        .getRecipeFor(MutationRecipe.Type.INSTANCE, new SimpleContainer(inputStack), server);

                if (match.isPresent()) {
                    ItemStack result = match.get().assemble(new SimpleContainer(inputStack), server.registryAccess());

                    server.destroyBlock(targetPos, false);
                    if (!result.isEmpty()) {
                        server.addFreshEntity(new ItemEntity(
                                server,
                                targetPos.getX() + 0.5,
                                targetPos.getY() + 0.25,
                                targetPos.getZ() + 0.5,
                                result.copy()
                        ));
                    }
                    emitEffects(server, targetPos);
                    anyConverted = true;
                }
            }
        }

        if (anyConverted) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
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