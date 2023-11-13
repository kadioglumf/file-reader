package com.kadioglumf.util.excel;

import com.kadioglumf.annotations.excel.ExcelColumn;
import com.kadioglumf.enums.FileExtension;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.DateUtils;
import com.kadioglumf.util.NumberFormatUtils;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public <T> List<T> read(Class<T> clazz, boolean isFirstRowHeader) throws Exception { //TODO exception özelleştir
        List<T> list = new ArrayList<>();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

            Sheet sheet = workbook.getSheetAt(i);
            Iterator<Row> rowIterator = sheet.rowIterator();

            if (isFirstRowHeader && rowIterator.hasNext()) {
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
        Field[] fields = ReflectionUtil.getAllFields(instance.getClass());
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
                    return parseDateValue(field, cell);
                } else if (isCellNumericFormatted(field.getType())) {
                    return parseNumericValue(cellValue, field.getType());
                }
                return cell.getNumericCellValue();
            case STRING:
                if (isCellNumericFormatted(field.getType())) {
                    return parseNumericValue(cellValue, field.getType());
                }
                else if (isCellDateFormatted(field.getType())) {
                    return parseStringToDateFormatted(field.getType(), cellValue);
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
        return Number.class.isAssignableFrom(type);
    }

    private Object parseNumericValue(String cellValue, Class<?> targetType) throws ParseException {
        if (targetType == BigDecimal.class) {
            return NumberFormatUtils.parseBigDecimal(cellValue);
        } else if (targetType == Integer.class || targetType == int.class) {
            return NumberFormatUtils.parseInteger(cellValue);
        } else if (targetType == Long.class || targetType == long.class) {
            return NumberFormatUtils.parseLong(cellValue);
        } else if (targetType == Double.class || targetType == double.class) {
            return NumberFormatUtils.parseDouble(cellValue);
        }
        else {
            throw new IllegalArgumentException("Unsupported numeric type: " + targetType.getName());
        }
    }

    private Object parseDateValue(Field field, Cell cell) {
        if (field.getType() == ZonedDateTime.class) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()); //TODO gmt ye göre ayarla
        }
        else if (field.getType() == Date.class) {
            return cell.getDateCellValue();
        }
        else if (field.getType() == LocalDate.class) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        else if (field.getType() == LocalDateTime.class) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        else if (field.getType() == Instant.class) {
            return cell.getDateCellValue().toInstant();
        }
        throw new RuntimeException(""); //TODO
    }

    private boolean isCellDateFormatted(Class<?> type) {
        return Temporal.class.isAssignableFrom(type)
                || Date.class.isAssignableFrom(type);
    }

    private Object parseStringToDateFormatted(Class<?> targetType, String cellValue) {
        if (targetType == ZonedDateTime.class) {
            return ZonedDateTime.parse(cellValue, DateTimeFormatter.ofPattern(DateUtils.determineDateFormatPattern(cellValue)));
        }
        else if (targetType == LocalDate.class) {
            return DateUtils.parseStringToLocalDate(cellValue);
        }
        else if (targetType == Date.class) {
            return DateUtils.parseStringToDate(cellValue);
        }
        else if (targetType == LocalDateTime.class) {
            return DateUtils.parseStringToLocalDateTime(cellValue);
        }
        else if (targetType == LocalTime.class) {
            return DateUtils.parseStringToLocalTime(cellValue);
        }
        else if (targetType == Instant.class) {
            return DateUtils.parseStringToInstant(cellValue);
        }
        throw new RuntimeException();//TODO
    }
}
