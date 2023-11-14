package com.kadioglumf.cellprocessor;

public class StringCell implements CellProcessor {


    @Override
    public Object execute(Object var1, Class<?> targetType) {
        if (var1 == null) {
            return null;
        }
        return String.valueOf(var1);
    }
}
