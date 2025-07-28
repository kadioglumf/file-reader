package com.kadioglumf.util.csv;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.csv.ImportCsvSettings;
import com.kadioglumf.cellprocessor.CellProcessor;
import com.kadioglumf.dto.BaseDto;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.ReflectionUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CsvReaderUtils extends BaseReaderUtils {

  private final CsvListReader csvListReader;
  private final ImportCsvSettings csvSettings;

  public CsvReaderUtils(InputStream inputStream, Class<?> clazz) {
    Field field = clazz.getDeclaredFields()[0];
    Class<?> fieldClass = ReflectionUtil.extractGenericType(field);
    this.csvSettings = getImportCsvSettings(fieldClass);
    CsvPreference csvPreference = buildCsvPreference(csvSettings);
    this.csvListReader = new CsvListReader(new InputStreamReader(inputStream), csvPreference);
  }

  @Override
  public <T extends BaseDto> T read(Class<T> clazz) throws Exception {
    T resultInstance = ReflectionUtil.instantiate(clazz);
    Field listField = clazz.getDeclaredFields()[0];
    Class<T> fieldClass = ReflectionUtil.extractGenericType(listField);

    List<T> dataList = readDataRows(fieldClass);

    setFieldValue(listField, resultInstance, dataList);
    return resultInstance;
  }

  private <T extends BaseDto> List<T> readDataRows(Class<T> clazz) throws Exception {
    Map<String, Integer> headers = readHeadersIfPresent();
    List<Field> fields = ReflectionUtil.getSortedFields(clazz, CsvColumn.class);

    List<T> dataList = new ArrayList<>();
    List<String> record;

    while ((record = csvListReader.read()) != null) {
      T instance = ReflectionUtil.instantiate(clazz);
      populateInstance(fields, instance, record, headers);
      dataList.add(instance);
    }

    return dataList;
  }

  private Map<String, Integer> readHeadersIfPresent() throws Exception {
    if (!csvSettings.isFirstRowHeader()) {
      return null;
    }

    List<String> headerRow = csvListReader.read();
    if (headerRow == null) {
      throw new RuntimeException("CSV file is empty or missing header row.");
    }

    return IntStream.range(0, headerRow.size())
        .boxed()
        .collect(Collectors.toMap(headerRow::get, i -> i, (a, b) -> b, CaseInsensitiveMap::new));
  }

  private <T extends BaseDto> void populateInstance(
      List<Field> fields, T instance, List<String> record, Map<String, Integer> headers)
      throws Exception {
    for (Field field : fields) {
      CsvColumn csvColumn = ReflectionUtil.getAnnotation(field, CsvColumn.class);
      CellProcessor processor = csvColumn.cellProcessor().getDeclaredConstructor().newInstance();

      int columnIndex = resolveColumnIndex(csvColumn, headers);
      Object value = processor.execute(record.get(columnIndex), field.getType());

      if (value != null) {
        setFieldValue(field, instance, value);
      }
    }
  }

  private int resolveColumnIndex(CsvColumn csvColumn, Map<String, Integer> headers) {
    if (csvColumn.columnIndex() != -1) {
      return csvColumn.columnIndex();
    }
    if (headers == null
        || StringUtils.isBlank(csvColumn.columnName())
        || !headers.containsKey(csvColumn.columnName())) {
      throw new RuntimeException(
          "Invalid column configuration for columnName: " + csvColumn.columnName());
    }
    return headers.get(csvColumn.columnName());
  }

  private ImportCsvSettings getImportCsvSettings(Class<?> clazz) {
    ImportCsvSettings settings = clazz.getAnnotation(ImportCsvSettings.class);
    if (settings == null) {
      throw new RuntimeException("Missing @ImportCsvSettings on class: " + clazz.getName());
    }
    return settings;
  }

  private CsvPreference buildCsvPreference(ImportCsvSettings settings) {
    return new CsvPreference.Builder(
            settings.quoteChar(), settings.delimiterChar(), settings.endOfLineSymbols())
        .build();
  }

  private void setFieldValue(Field field, Object instance, Object value) {
    try {
      String setterName = "set" + StringUtils.capitalize(field.getName());
      Method setter = instance.getClass().getMethod(setterName, field.getType());
      setter.invoke(instance, value);
    } catch (NoSuchMethodException e) {
      trySetFieldDirectly(field, instance, value);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set value for field: " + field.getName(), e);
    }
  }

  private void trySetFieldDirectly(Field field, Object instance, Object value) {
    try {
      ReflectionUtils.makeAccessible(field);
      ReflectionUtils.setField(field, instance, value);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set field directly: " + field.getName(), e);
    }
  }
}
