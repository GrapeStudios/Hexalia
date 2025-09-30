package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.recipe.ModRecipes;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class MutavisItem extends Item {

    public MutavisItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        ItemStack inputStack = new ItemStack(world.getBlockState(pos).getBlock().asItem());
        if (inputStack.isEmpty()) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            MutationRecipeInput input = new MutationRecipeInput(inputStack);

            Optional<RecipeEntry<MutationRecipe>> match =
                    world.getRecipeManager().getFirstMatch(ModRecipes.MUTATION_TYPE, input, world);

            if (match.isPresent()) {
                world.breakBlock(pos, false);

                ServerWorld server = (ServerWorld) world;

                ItemStack result = match.get().value().getResult(server.getRegistryManager());
                if (!result.isEmpty()) {
                    server.spawnEntity(new ItemEntity(
                            server,
                            pos.getX() + 0.5,
                            pos.getY() + 0.25,
                            pos.getZ() + 0.5,
                            result.copy()
                    ));
                }

                PlayerEntity player = context.getPlayer();
                if (player != null && !player.getAbilities().creativeMode) {
                    context.getStack().decrement(1);
                }

                emitEffects(server, pos);
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }

    private void emitEffects(ServerWorld server, BlockPos pos) {
        server.playSound(
                null,
                pos,
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.BLOCKS,
                0.5F, 1.0F
        );

        ParticleEffect leaves = ModParticleType.LEAVES;
        server.spawnParticles(
                leaves,
                pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                15,
                0.2, 0.25, 0.2,
                0.0
        );
    }
}