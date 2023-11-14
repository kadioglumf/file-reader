package com.kadioglumf.util.csv;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.csv.ExportCsvSettings;
import com.kadioglumf.cellprocessor.CellProcessor;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.ReflectionUtil;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderUtils extends BaseReaderUtils {

    private final CsvListReader csvListReader;
    private final ExportCsvSettings csvSettings;

    public CsvReaderUtils(InputStream inputStream, Class<?> clazz) {
        ExportCsvSettings csvSettings = clazz.getAnnotation(ExportCsvSettings.class);
        if (csvSettings == null) {
            throw new RuntimeException(""); //TODO
        }
        this.csvSettings = csvSettings;

        CsvPreference csvPreference = new CsvPreference.Builder(csvSettings.quoteChar(), csvSettings.delimiterChar(), csvSettings.endOfLineSymbols()).build();
        this.csvListReader = new CsvListReader(new InputStreamReader(inputStream), csvPreference);
    }

    @Override
    public <T> List<T> read(Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();

        List<String> record;
        if (csvSettings.isFirstRowHeader()) {
            csvListReader.read();
        }

        List<Field> fields = ReflectionUtil.getSortedFields(clazz, CsvColumn.class);
        while ((record = csvListReader.read()) != null) {

            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : fields) {
                CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
                CellProcessor cellProcessor = csvColumn.cellProcessor().getDeclaredConstructor().newInstance();
                Object value = cellProcessor.execute(record.get(csvColumn.columnIndex()), field.getType());
                if (value != null) {
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
            list.add(instance);
        }

        return list;
    }

}
