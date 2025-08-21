package net.astralya.hexalia.item.custom;

import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.variant.SilkMothVariant;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BottledMothItem extends Item {

    public static final String MOTH_NAME = "EntityName";

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

            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null) {
                SilkMothVariant variant = SilkMothVariant.byId(nbt.getInt("SilkMothVariant"));
                silkMothEntity.setVariant(variant);
                silkMothEntity.readNbt(nbt);
            }

            silkMothEntity.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            world.spawnEntity(silkMothEntity);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

            itemStack.decrement(1);
            player.getInventory().insertStack(new ItemStack(ModItems.RUSTIC_BOTTLE));

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains(MOTH_NAME)) {
                String entityName = nbt.getString(MOTH_NAME);
                MutableText nameTooltip = Text.translatable("tooltip.hexalia.bottled_moth", entityName)
                        .formatted(Formatting.ITALIC, Formatting.GREEN);
                tooltip.add(nameTooltip);
            }
        }
    }
}
