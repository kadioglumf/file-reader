package com.kadioglumf.util.excel;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.annotations.excel.ExportExcelSettings;
import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.DateUtils;
import com.kadioglumf.util.NumberUtils;
import com.kadioglumf.util.ReflectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public final class ExcelReaderUtil extends BaseReaderUtils {

    private final Workbook workbook;
    private final DataFormatter dataFormatter;

    public ExcelReaderUtil(InputStream inputStream, FileExtension fileExtension) throws IOException {
        if (FileExtension.XLS.equals(fileExtension)) {
            this.workbook = new HSSFWorkbook(inputStream);
        }
        else {
            this.workbook = new XSSFWorkbook(inputStream);
        }
        this.dataFormatter = new DataFormatter(LocaleContextHolder.getLocale()); //TODO
    }

    public <T> List<T> read(Class<T> clazz) throws Exception { //TODO exception özelleştir
        List<T> list = new ArrayList<>();

        ExportExcelSettings excelSettings = clazz.getAnnotation(ExportExcelSettings.class);
        if (excelSettings == null) {
            throw new RuntimeException(""); //TODO
        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

            Sheet sheet = workbook.getSheetAt(i);
            Iterator<Row> rowIterator = sheet.rowIterator();

            if (excelSettings.isFirstRowHeader() && rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                addRow(clazz, list, row);
            }
        }
        return list;
    }

    private <T> void addRow(Class<T> clazz, List<T> list, Row row) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        List<Field> fields = ReflectionUtil.getSortedFields(instance.getClass(), ExcelColumn.class);
        boolean isAllCellsEmpty = true;
        for (Field field : fields) {

            Cell cell = getCell(field, row);
            if (cell != null) {
                Object value = getCellValue(cell, field);
                if (value != null) {
                    isAllCellsEmpty = false;
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
        }

        if (!isAllCellsEmpty) {
            list.add(instance);
        }
    }

    private Cell getCell(Field field, Row row) {
        ExcelColumn ec = field.getAnnotation(ExcelColumn.class);
        if (ec != null) {
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                if (ec.columnIndex() == cell.getColumnIndex()) {
                    return cell;
                }
            }
        }
        return null;
    }

    private Object getCellValue(Cell cell, Field field) throws Exception {
        String cellValue = this.dataFormatter.formatCellValue(cell);
        if (StringUtils.isBlank(cellValue)) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell, null)) {
                    return DateUtils.parseStringToDateFormatted(field.getType(), cellValue);
                }
                else if (isCellNumericFormatted(field.getType())) {
                    return NumberUtils.parseNumericValue(cellValue, field.getType());
                }
                return cell.getNumericCellValue();
            case STRING:
                if (isCellNumericFormatted(field.getType())) {
                    return NumberUtils.parseNumericValue(cellValue, field.getType());
                }
                else if (isCellDateFormatted(field.getType())) {
                    return DateUtils.parseStringToDateFormatted(field.getType(), cellValue);
                }

                return cell.getRichStringCellValue().getString();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
                return null;
            case ERROR:
                return FormulaError.forInt(cell.getErrorCellValue()).getString();
            default:
                throw new RuntimeException("Unexpected celltype (" + cell.getCellType() + ")");
        }
    }

    private boolean isCellNumericFormatted(Class<?> type) {
        return Number.class.isAssignableFrom(type)
                || type == int.class || type == double.class
                || type == float.class || type == long.class
                || type == short.class || type == byte.class;
    }

    private boolean isCellDateFormatted(Class<?> type) {
        return Temporal.class.isAssignableFrom(type)
                || Date.class.isAssignableFrom(type);
    }
}
