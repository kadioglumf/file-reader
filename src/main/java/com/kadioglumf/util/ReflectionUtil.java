package com.kadioglumf.util;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.dto.Tuple;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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

    public static List<Field> getSortedFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> declaredFields = Stream.of(clazz.getDeclaredFields())
                .filter(p -> p.getAnnotation(annotation) != null)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(declaredFields)) {
            return new ArrayList<>();
        }

        if (annotation.isAssignableFrom(CsvColumn.class)) {
            return declaredFields.stream()
                    .map(f -> new Tuple<>(f.getAnnotation(CsvColumn.class).columnIndex(), f))
                    .sorted(Comparator.comparing(Tuple::getFirst))
                    .map(Tuple::getSecond)
                    .collect(Collectors.toList());
        }
        else if (annotation.isAssignableFrom(ExcelColumn.class)) {
            return declaredFields.stream()
                    .map(f -> new Tuple<>(f.getAnnotation(ExcelColumn.class).columnIndex(), f))
                    .sorted(Comparator.comparing(Tuple::getFirst))
                    .map(Tuple::getSecond)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
