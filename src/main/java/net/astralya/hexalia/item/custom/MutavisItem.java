package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.MutationRecipeInput;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.util.MutationOutput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
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
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();
        boolean success = tryMutate((ServerWorld) world, pos, stack, context.getPlayer());
        return success ? ActionResult.CONSUME : ActionResult.PASS;
    }

    public boolean tryMutate(ServerWorld world, BlockPos pos, ItemStack mutavisStack, PlayerEntity player) {
        ItemStack inputStack = new ItemStack(world.getBlockState(pos).getBlock().asItem());
        if (inputStack.isEmpty()) {
            return false;
        }
        Optional<MutationRecipe> match = world.getRecipeManager()
                .getFirstMatch(ModRecipes.MUTATION_TYPE, new MutationRecipeInput(inputStack), world)
                .map(RecipeEntry::value);
        if (match.isEmpty()) {
            return false;
        }
        world.breakBlock(pos, false);
        ItemStack out = match.get().craft(new MutationRecipeInput(inputStack), world.getRegistryManager());
        MutationOutput.apply(world, pos, out);
        if (player == null || !player.getAbilities().creativeMode) {
            mutavisStack.decrement(1);
        }
        emitEffects(world, pos);
        return true;
    }

    public void emitEffects(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 0.5F, 1.0F);
        world.spawnParticles(
                ModParticleType.LEAVES,
                pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                15, 0.2, 0.25, 0.2, 0.0
        );
    }
}