package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.particle.ModParticleType;
import net.astralya.hexalia.recipe.ModRecipes;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleEffect;
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
        BlockState state = world.getBlockState(pos);

        ItemStack inputStack = new ItemStack(state.getBlock().asItem());
        if (inputStack.isEmpty()) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            SimpleInventory inv = new SimpleInventory(inputStack);

            Optional<MutationRecipe> match = world.getRecipeManager()
                    .getFirstMatch(ModRecipes.MUTATION_TYPE, inv, world);

            if (match.isPresent()) {
                world.breakBlock(pos, false);

                ServerWorld server = (ServerWorld) world;
                ItemStack out = match.get().craft(inv, server.getRegistryManager());

                if (!out.isEmpty()) {
                    server.spawnEntity(new ItemEntity(
                            server,
                            pos.getX() + 0.5,
                            pos.getY() + 0.25,
                            pos.getZ() + 0.5,
                            out.copy()
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
