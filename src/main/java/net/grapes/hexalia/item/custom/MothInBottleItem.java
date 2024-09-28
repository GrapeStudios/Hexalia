package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MothInBottleItem extends Item {

    public MothInBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();

        if (!world.isClient && player != null) {
            // Obtener la posición donde se soltará la polilla
            BlockPos spawnPos = blockPos.offset(direction);

            // Crear la entidad SilkMoth en el mundo
            SilkMothEntity silkMothEntity = new SilkMothEntity(ModEntities.SILK_MOTH, world);
            silkMothEntity.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            world.spawnEntity(silkMothEntity);

            // Reproduce el sonido de botella vaciada
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Remover la botella con la polilla y devolver una botella vacía
            if (!player.isCreative()) {
                itemStack.decrement(1);
                player.getInventory().insertStack(new ItemStack(ModItems.RUSTIC_BOTTLE));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
