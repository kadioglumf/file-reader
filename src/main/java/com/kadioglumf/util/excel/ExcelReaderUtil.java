package com.kadioglumf.util.excel;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.ImportExcelSettings;
import com.kadioglumf.cellprocessor.CellProcessor;
import com.kadioglumf.dto.BaseDto;
import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.ReflectionUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ReflectionUtils;

@Getter
@Setter
public final class ExcelReaderUtil extends BaseReaderUtils {

  private final Workbook workbook;
  private final FormulaEvaluator formulaEvaluator;
  private final DataFormatter dataFormatter;

  public ExcelReaderUtil(InputStream inputStream, FileExtension fileExtension) throws IOException {
    this.workbook = createWorkbook(inputStream, fileExtension);
    this.formulaEvaluator = createFormulaEvaluator(workbook, fileExtension);
    this.dataFormatter = new DataFormatter(LocaleContextHolder.getLocale());
  }

  private Workbook createWorkbook(InputStream inputStream, FileExtension fileExtension)
      throws IOException {
    return FileExtension.XLS.equals(fileExtension)
        ? new HSSFWorkbook(inputStream)
        : new XSSFWorkbook(inputStream);
  }

  private FormulaEvaluator createFormulaEvaluator(Workbook workbook, FileExtension fileExtension) {
    return FileExtension.XLS.equals(fileExtension)
        ? new HSSFFormulaEvaluator((HSSFWorkbook) workbook)
        : new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
  }

  public <T extends BaseDto> T read(Class<T> clazz) throws Exception {
    T instance = ReflectionUtil.instantiate(clazz);
    List<Field> sheetFields =
        ReflectionUtil.getSortedFields(clazz, com.kadioglumf.annotations.excel.Sheet.class);

    for (Field field : sheetFields) {
      processSheetField(instance, field);
    }

    return instance;
  }

  private <T extends BaseDto> void processSheetField(T instance, Field field) throws Exception {
    Class<T> fieldClass = ReflectionUtil.extractGenericType(field);
    ImportExcelSettings settings =
        ReflectionUtil.getAnnotation(fieldClass, ImportExcelSettings.class);
    com.kadioglumf.annotations.excel.Sheet sheetAnnotation =
        ReflectionUtil.getAnnotation(field, com.kadioglumf.annotations.excel.Sheet.class);
    Sheet sheet = workbook.getSheetAt(sheetAnnotation.index());
    Map<String, Integer> headers = parseHeaders(sheet, settings);

    List<Object> rowData = readSheetData(sheet, fieldClass, headers, settings.isFirstRowHeader());
    setFieldValue(field, instance, rowData);
  }

  private Map<String, Integer> parseHeaders(Sheet sheet, ImportExcelSettings settings) {
    Map<String, Integer> headers = new CaseInsensitiveMap<>();
    if (!settings.isFirstRowHeader()) {
      return headers;
    }

    Iterator<Row> rowIterator = sheet.rowIterator();
    if (!rowIterator.hasNext()) {
      return headers;
    }

    Row headerRow = rowIterator.next();
    for (Cell cell : headerRow) {
      headers.put(cell.getStringCellValue(), cell.getColumnIndex());
    }

    return headers;
  }

  private List<Object> readSheetData(
      Sheet sheet, Class<?> fieldClass, Map<String, Integer> headers, boolean isFirstRowHeader)
      throws Exception {
    List<Object> dataList = new ArrayList<>();
    Iterator<Row> rowIterator = sheet.rowIterator();

    if (isFirstRowHeader && rowIterator.hasNext()) {
      rowIterator.next();
    }

    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Object rowObject = readRow(fieldClass, row, headers, isFirstRowHeader);
      if (rowObject != null) {
        dataList.add(rowObject);
      }
    }

    return dataList;
  }

  private Object readRow(
      Class<?> clazz, Row row, Map<String, Integer> headers, boolean isFirstRowHeader)
      throws Exception {
    Object instance = ReflectionUtil.instantiate(clazz);
    List<Field> fields = ReflectionUtil.getSortedFields(clazz, ExcelColumn.class);

    boolean hasValue = false;
    for (Field field : fields) {
      ExcelColumn column = ReflectionUtil.getAnnotation(field, ExcelColumn.class);
      int columnIndex = resolveColumnIndex(column, headers, isFirstRowHeader);
      Object cellValue = getCellValue(row.getCell(columnIndex), field);
      if (cellValue != null) {
        hasValue = true;
        setFieldValue(field, instance, cellValue);
      }
    }

    return hasValue ? instance : null;
  }

  private Object getCellValue(Cell cell, Field field) throws Exception {
    if (cell == null) {
      return null;
    }
    String rawValue = dataFormatter.formatCellValue(cell, formulaEvaluator);
    if (StringUtils.isBlank(rawValue)) {
      return null;
    }

    ExcelColumn column = ReflectionUtil.getAnnotation(field, ExcelColumn.class);
    CellProcessor processor = column.cellProcessor().getDeclaredConstructor().newInstance();
    return processor.execute(rawValue, field.getType());
  }

  private int resolveColumnIndex(
      ExcelColumn column, Map<String, Integer> headers, boolean isFirstRowHeader) {
    if (column.columnIndex() != -1) {
      return column.columnIndex();
    }
    if (!isFirstRowHeader
        || StringUtils.isBlank(column.columnName())
        || !headers.containsKey(column.columnName())) {
      throw new RuntimeException("Invalid column configuration: " + column.columnName());
    }
    return headers.get(column.columnName());
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
