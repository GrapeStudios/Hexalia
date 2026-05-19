package net.astralya.hexalia.item.custom;

import java.util.List;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BottleMothItem extends Item {

  public BottleMothItem(Properties properties) {
    super(properties);
  }

  private static MothData ensureData(ItemStack stack) {
    MothData data = stack.get(ModComponents.MOTH.get());
    if (data == null) {
      data = new MothData("", 0);
      stack.set(ModComponents.MOTH.get(), data);
    }
    return data;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    Player player = context.getPlayer();
    ItemStack stack = context.getItemInHand();
    BlockPos pos = context.getClickedPos();
    Direction face = context.getClickedFace();

    if (!level.isClientSide && player != null) {
      BlockPos spawnPos = pos.relative(face);

      SilkMothEntity moth = new SilkMothEntity(ModEntities.SILK_MOTH.get(), level);

      MothData data = ensureData(stack);
      moth.setVariant(SilkMothVariant.byId(data.variantId()));

      if (!data.name().isEmpty()) {
        moth.setCustomName(Component.literal(data.name()));
      }

      moth.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
      level.addFreshEntity(moth);

      level.playSound(
          null,
          player.getX(),
          player.getY(),
          player.getZ(),
          SoundEvents.ITEM_PICKUP,
          SoundSource.PLAYERS,
          1.0F,
          1.0F);

      stack.shrink(1);
      player.getInventory().add(new ItemStack(ModItems.RUSTIC_BOTTLE.get()));

      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext ctx, List<Component> lines, TooltipFlag flag) {
    MothData data = ensureData(stack);
    if (!data.name().isEmpty()) {
      lines.add(
          Component.translatable("tooltip.hexalia.bottled_moth", data.name())
              .withStyle(Style.EMPTY.withItalic(true).withColor(0x55FF55)));
    }
  }
}
