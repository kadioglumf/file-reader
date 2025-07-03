package com.kadioglumf.cellprocessor;

public class BooleanCell implements CellProcessor {

  @Override
  public Object execute(Object var1, Class<?> targetType) {
    if (var1 == null) {
      return null;
    }
    return Boolean.parseBoolean(var1.toString());
  }
}
