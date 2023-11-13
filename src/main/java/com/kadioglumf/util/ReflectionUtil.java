package com.kadioglumf.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class ReflectionUtil {

    public static Field[] getAllFields(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Field[] result = getAllFields(clazz.getSuperclass());
        if (result == null) {
            return clazz.getDeclaredFields();
        }
        else {
            return Stream.concat(Arrays.stream(result), Arrays.stream(clazz.getDeclaredFields()))
                    .toArray(size -> (Field[]) Array.newInstance(result.getClass().getComponentType(), size));
        }
    }
}
