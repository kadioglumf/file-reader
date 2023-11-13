package com.kadioglumf.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class NumberFormatUtils {

    private static final Map<String, String> EN_NUMBER_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{0,100},?\\d{1,3}.\\d{2}$", "#,##0.###");
        put("^\\d{0,100},?\\d{1,3}.\\d{2}?$", "#,##0.000");
        put("^\\d{0,100},\\d{1,3}.\\d{2}$", "#,###.##");
        put("^\\d{1,3}$", "###");
    }};

    private static final Map<String, String> TR_NUMBER_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{0,100}.?\\d{1,3},\\d{2}$", "#.##0,###");
        put("^\\d{0,100}.?\\d{1,3},\\d{2}?$", "#.##0,000");
        put("^\\d{0,100}.\\d{1,3},\\d{2}$", "#.###,##");
        put("^\\d{1,3}$", "###");
    }};

    public static BigDecimal parseBigDecimal(String value) throws ParseException {
        Map<String, String> regexps = "tr".equals(LocaleContextHolder.getLocale().getLanguage())
                ? TR_NUMBER_FORMAT_REGEXPS
                : EN_NUMBER_FORMAT_REGEXPS;

        for (String regexp : regexps.keySet()) {
            if (value.toLowerCase().matches(regexp)) {
                String pattern = regexps.get(regexp);

                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(new DecimalFormatSymbols(LocaleContextHolder.getLocale()));
                df.applyLocalizedPattern(pattern);
                df.setParseBigDecimal(true);
                return (BigDecimal) df.parse(value);
            }
        }
        throw new RuntimeException(""); //TODO
    }

    public static Integer parseInteger(String value) {
        return Integer.parseInt(value);
    }

    public static Long parseLong(String value) {
        return  Long.parseLong(value);
    }

    public static Double parseDouble(String value) {
        return Double.parseDouble(value);
    }
}
