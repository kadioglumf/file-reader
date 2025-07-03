package com.kadioglumf.util;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.Sheet;
import com.kadioglumf.dto.Tuple;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtil {

  public static Field[] getAllFields(Class clazz) {
    if (clazz == null) {
      return null;
    }
    Field[] result = getAllFields(clazz.getSuperclass());
    if (result == null) {
      return clazz.getDeclaredFields();
    } else {
      return Stream.concat(Arrays.stream(result), Arrays.stream(clazz.getDeclaredFields()))
          .toArray(size -> (Field[]) Array.newInstance(result.getClass().getComponentType(), size));
    }
  }

  public static List<Field> getSortedFields(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    List<Field> declaredFields =
        Stream.of(clazz.getDeclaredFields())
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
    } else if (annotation.isAssignableFrom(ExcelColumn.class)) {
      return declaredFields.stream()
          .map(f -> new Tuple<>(f.getAnnotation(ExcelColumn.class).columnIndex(), f))
          .sorted(Comparator.comparing(Tuple::getFirst))
          .map(Tuple::getSecond)
          .collect(Collectors.toList());
    } else if (annotation.isAssignableFrom(Sheet.class)) {
      return declaredFields.stream()
          .map(f -> new Tuple<>(f.getAnnotation(Sheet.class).index(), f))
          .sorted(Comparator.comparing(Tuple::getFirst))
          .map(Tuple::getSecond)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  public static <T> T instantiate(Class<T> clazz) throws Exception {
    return clazz.getDeclaredConstructor().newInstance();
  }

  public static <A extends Annotation> A getAnnotation(
      AnnotatedElement element, Class<A> annotationClass) {
    A annotation = element.getAnnotation(annotationClass);
    if (annotation == null) {
      throw new RuntimeException("Missing required annotation: " + annotationClass.getSimpleName());
    }
    return annotation;
  }

  public static <T> Class<T> extractGenericType(Field field) {
    Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    if (genericType instanceof Class<?>) {
      return (Class<T>) genericType;
    }
    throw new RuntimeException("Invalid generic type for field: " + field.getName());
  }
}
