package net.astralya.hexalia.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.astralya.hexalia.Hexalia;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ModWoodTypes {
  public static final WoodType COTTONWOOD =
      register(new WoodType(Hexalia.MOD_ID + ":cottonwood", BlockSetType.OAK));
  public static final WoodType WILLOW =
      register(new WoodType(Hexalia.MOD_ID + ":willow", BlockSetType.OAK));

  private ModWoodTypes() {}

  public static void init() {}

  private static WoodType register(WoodType woodType) {
    try {
      Method register = WoodType.class.getDeclaredMethod("register", WoodType.class);
      register.setAccessible(true);
      return (WoodType) register.invoke(null, woodType);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
      throw new IllegalStateException("Unable to register wood type " + woodType.name(), exception);
    }
  }
}
