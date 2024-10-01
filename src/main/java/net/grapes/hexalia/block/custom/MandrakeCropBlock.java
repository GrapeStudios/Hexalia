package net.grapes.hexalia.block.custom;

import net.grapes.hexalia.effect.ModEffects;
import net.grapes.hexalia.item.ModItems;
import net.grapes.hexalia.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;

public class MandrakeCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = IntProperty.of("age", 0, 3);

    public MandrakeCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.MANDRAKE_SEEDS;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient() && state.get(AGE) == MAX_AGE) {
            if (!(player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.EARPLUGS))) {
                player.addStatusEffect(new StatusEffectInstance(ModEffects.STUNNED, 60, 0));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MANDRAKE_SCREAM,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }
}