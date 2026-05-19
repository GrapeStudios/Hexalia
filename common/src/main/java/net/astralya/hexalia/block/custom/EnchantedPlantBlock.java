package net.astralya.hexalia.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.astralya.hexalia.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantedPlantBlock extends BushBlock {
  public static final MapCodec<EnchantedPlantBlock> CODEC = simpleCodec(EnchantedPlantBlock::new);
  protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

  public EnchantedPlantBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BushBlock> codec() {
    return CODEC;
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return state.is(BlockTags.DIRT) || state.is(ModBlocks.INFUSED_DIRT.get());
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Vec3 offset = state.getOffset(level, pos);
    return SHAPE.move(offset.x, offset.y, offset.z);
  }

  @Override
  public void appendHoverText(
      ItemStack stack,
      Item.TooltipContext context,
      List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(
        Component.translatable("tooltip.hexalia.enchanted_plant").withStyle(ChatFormatting.GRAY));
  }
}
