package net.astralya.hexalia.item.custom;

import java.lang.reflect.Method;
import net.astralya.hexalia.Hexalia;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VerdantGrimoireItem extends Item {
  private static final ResourceLocation BOOK_ID =
      ResourceLocation.fromNamespaceAndPath(Hexalia.MOD_ID, "verdant_grimoire");

  public VerdantGrimoireItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
      if (!tryOpenPatchouliBook(serverPlayer)) {
        player.displayClientMessage(
            Component.translatable("message.hexalia.patchouli_missing"), true);
      }
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
  }

  private static boolean tryOpenPatchouliBook(ServerPlayer player) {
    try {
      Class<?> apiClass = Class.forName("vazkii.patchouli.api.PatchouliAPI");
      Object api = apiClass.getMethod("get").invoke(null);
      Class<?> apiInterface = Class.forName("vazkii.patchouli.api.PatchouliAPI$IPatchouliAPI");
      Method openBook =
          apiInterface.getMethod("openBookGUI", ServerPlayer.class, ResourceLocation.class);
      openBook.invoke(api, player, BOOK_ID);
      return true;
    } catch (ReflectiveOperationException | LinkageError ignored) {
      return false;
    }
  }
}
