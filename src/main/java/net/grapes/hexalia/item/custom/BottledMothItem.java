package net.grapes.hexalia.item.custom;

import net.grapes.hexalia.entity.ModEntities;
import net.grapes.hexalia.entity.custom.SilkMothEntity;
import net.grapes.hexalia.entity.variant.SilkMothVariant;
import net.grapes.hexalia.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BottledMothItem extends Item {

    public BottledMothItem(Settings settings) {
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
            BlockPos spawnPos = blockPos.offset(direction);
            SilkMothEntity silkMothEntity = new SilkMothEntity(ModEntities.SILK_MOTH, world);

            // Leer los datos NBT del ItemStack
            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null) {
                // Recuperar la variante del Silk Moth del NBT
                SilkMothVariant variant = SilkMothVariant.byId(nbt.getInt("SilkMothVariant"));
                silkMothEntity.setVariant(variant); // Establecer la variante en la entidad Silk Moth

                // (Opcional) Establecer cualquier otro dato relevante desde el NBT
                silkMothEntity.readNbt(nbt);
            }

            // Configurar la posición de la entidad Silk Moth
            silkMothEntity.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            world.spawnEntity(silkMothEntity); // Generar la entidad en el mundo

            // Reproducir un sonido de efecto
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Eliminar la botella con la polilla y agregar una botella vacía
            itemStack.decrement(1);
            player.getInventory().insertStack(new ItemStack(ModItems.RUSTIC_BOTTLE));

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
