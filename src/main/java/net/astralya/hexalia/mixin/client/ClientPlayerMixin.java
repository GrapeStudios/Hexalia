package net.astralya.hexalia.mixin.client;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.RootshaperItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerMixin {

    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void hexalia$rootshaperModeSync(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        ClientPlayerEntity player = client.player;
        ItemStack stack = player.getMainHandStack();
        if (!stack.isOf(ModItems.ROOTSHAPER)) return;
        BlockState state = player.getWorld().getBlockState(pos);
        int newMode = RootshaperItem.computeMode(state);
        int oldMode = RootshaperItem.getMode(stack);
        if (newMode != oldMode) {
            RootshaperItem.setMode(stack, newMode);
            RootshaperItem.playMorphSound(player.getWorld(), player);
        }
    }
}