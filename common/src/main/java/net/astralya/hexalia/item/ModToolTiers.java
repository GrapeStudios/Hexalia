package net.astralya.hexalia.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class ModToolTiers {
  public static final Tier ANCIENT =
      new Tier() {
        @Override
        public int getUses() {
          return 250;
        }

        @Override
        public float getSpeed() {
          return 8.0F;
        }

        @Override
        public float getAttackDamageBonus() {
          return 3.0F;
        }

        @Override
        public net.minecraft.tags.TagKey<Block> getIncorrectBlocksForDrops() {
          return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
          return 22;
        }

        @Override
        public Ingredient getRepairIngredient() {
          return Ingredient.of(ModItems.ANCIENT_SEED.get());
        }
      };

  private ModToolTiers() {}
}
