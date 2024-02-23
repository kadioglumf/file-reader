package com.kadioglumf.util.csv;

import com.kadioglumf.annotations.csv.CsvColumn;
import com.kadioglumf.annotations.csv.ImportCsvSettings;
import com.kadioglumf.cellprocessor.CellProcessor;
import com.kadioglumf.util.BaseReaderUtils;
import com.kadioglumf.util.ReflectionUtil;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvReaderUtils extends BaseReaderUtils {

    private final CsvListReader csvListReader;
    private final ImportCsvSettings csvSettings;

    public CsvReaderUtils(InputStream inputStream, Class<?> clazz) {
        ImportCsvSettings csvSettings = clazz.getAnnotation(ImportCsvSettings.class);
        if (csvSettings == null) {
            throw new RuntimeException(String.format("ExportCsvSettings not found with class: %s", clazz));
        }
        this.csvSettings = csvSettings;

        CsvPreference csvPreference = new CsvPreference.Builder(csvSettings.quoteChar(), csvSettings.delimiterChar(), csvSettings.endOfLineSymbols()).build();
        this.csvListReader = new CsvListReader(new InputStreamReader(inputStream), csvPreference);
    }

    @Override
    public <T> List<T> read(Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();

        List<String> record;
        Map<String, Integer> headers = null;
        boolean isFirstRowHeader = csvSettings.isFirstRowHeader();
        if (isFirstRowHeader) {
            record = csvListReader.read();

            headers = new CaseInsensitiveMap<>(IntStream.range(0, record.size())
                    .boxed()
                    .collect(Collectors.toMap(record::get, i -> i)));
        }

        List<Field> fields = ReflectionUtil.getSortedFields(clazz, CsvColumn.class);
        while ((record = csvListReader.read()) != null) {

            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : fields) {
                CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
                CellProcessor cellProcessor = csvColumn.cellProcessor().getDeclaredConstructor().newInstance();
                Object value = cellProcessor.execute(record.get(getColumnIndex(csvColumn, headers, isFirstRowHeader)), field.getType());
                if (value != null) {
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
            list.add(instance);
        }

        return list;
    }

    private int getColumnIndex(CsvColumn csvColumn, Map<String, Integer> headers, boolean isFirstRowHeader) {
        if (csvColumn.columnIndex() != -1) {
            return csvColumn.columnIndex();
        }
        if (headers == null
                || StringUtils.isBlank(csvColumn.columnName())
                || headers.get(csvColumn.columnName()) == null
                || !isFirstRowHeader) {
            throw new RuntimeException("cannot have both column name and column order!");
        }
        return headers.get(csvColumn.columnName());
    }
}
