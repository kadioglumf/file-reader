package com.kadioglumf.cellprocessor;

public class EnumCell<T extends Enum<T>> implements CellProcessor {

  @Override
  public Object execute(Object var1, Class<?> targetType) {
    if (var1 == null) {
      return null;
    }
    if (!targetType.isEnum()) {
      throw new RuntimeException("Target type is not an enum: " + targetType.getName());
    }
    return Enum.valueOf((Class<T>) targetType, var1.toString());
  }
}
