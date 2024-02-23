package com.kadioglumf.util;

import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class DateUtils {

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
        put("^\\d{1,2}\\-\\d{1,2}\\-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$", "MM/dd/yyyy");
        put("^\\d{4}\\/\\d{1,2}\\/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    }};

    private static final Map<String, String> DATE_WITH_TIME_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}\\-\\d{1,2}\\-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{4}\\/\\d{1,2}\\/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\/\\d{1,2}\\/\\d{2,4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{14}$", "yyyyMMddHHmmss");
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yyyy HH:mm:ss");
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy.MM.dd HH:mm:ss");
        put("^\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{1,2}:\\d{2}$", "dd.MM.yyyy HH:mm");
        put("^\\d{1}\\.\\d{2}\\.\\d{4}\\s\\d{1,2}:\\d{2}$", "d.MM.yyyy HH:mm");
        put("^\\d{1,2}\\-\\d{1,2}\\-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        put("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        put("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd/MM/yyyy HH:mm:ss");
        put("^\\d{4}\\/\\d{1,2}\\/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
        put("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{6}$", "yyyy-MM-dd HH:mm:ss.SSSSSS");
        put("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}\\+\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }};

    private static final Map<String, String> TIME_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{1,2}:\\d{2}$", "HH:mm");
    }};

    private static final Map<String, String> DATE_ALL_FORMAT_REGEXPS = new HashMap<>() {{
        putAll(DATE_FORMAT_REGEXPS);
        putAll(DATE_WITH_TIME_FORMAT_REGEXPS);
    }};


    public static Object parseStringToDateFormatted(Class<?> targetType, String cellValue) {
        if (targetType == ZonedDateTime.class) {
            return DateUtils.parseStringToZonedDateTime(cellValue);
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
        throw new RuntimeException(String.format("targetType did not match with: %s", targetType));
    }

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     */
    private static String determineDateFormatPattern(String dateString) {
        for (String regexp : DATE_ALL_FORMAT_REGEXPS.keySet()) {
            if (dateString.matches(regexp)) {
                return DATE_ALL_FORMAT_REGEXPS.get(regexp);
            }
        }
        throw new RuntimeException(String.format("Not found regexp for : %s", dateString));
    }

    private static String determineTimeFormatPattern(String dateString) {
        for (String regexp : TIME_FORMAT_REGEXPS.keySet()) {
            if (dateString.matches(regexp)) {
                return TIME_FORMAT_REGEXPS.get(regexp);
            }
        }
        throw new RuntimeException(String.format("Not found regexp for : %s", dateString));
    }


    private static LocalDate parseStringToLocalDate(String dateStr) {
        try {
            String pattern = determineDateFormatPattern(dateStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception ex) {
            log.error("parseStringToLocalDate error: ", ex);
            throw new RuntimeException("");
        }
    }

    private static LocalDateTime parseStringToLocalDateTime(String dateTimeStr) {
        try {
            String pattern = determineDateFormatPattern(dateTimeStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception ex) {
            log.error("parseStringToLocalDateTime error: ", ex);
            throw new RuntimeException("");
        }
    }

    private static LocalTime parseStringToLocalTime(String dateTimeStr) {
        try {
            String pattern = determineTimeFormatPattern(dateTimeStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalTime.parse(dateTimeStr, formatter);
        } catch (Exception ex) {
            log.error("parseStringToLocalTime error: ", ex);
            throw new RuntimeException("");
        }
    }

    private static Date parseStringToDate(String date) {
        try {
            String pattern = determineDateFormatPattern(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(date);
        } catch (Exception ex) {
            log.error("parseStringToDate error: ", ex);
            throw new RuntimeException("");
        }
    }

    private static Instant parseStringToInstant(String date) {
        try {
            String pattern = determineDateFormatPattern(date);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return Instant.from(formatter.parse(date));
        } catch (DateTimeException ex) {
            log.error("parseStringToInstant error: ", ex);
            return parseStringToLocalDateTime(date).atZone(ZoneOffset.UTC).toInstant();
        } catch (Exception ex) {
            log.error("parseStringToInstant error: ", ex);
            throw new RuntimeException();
        }
    }

    private static ZonedDateTime parseStringToZonedDateTime(String dateStr) {
        try {
            String pattern = determineDateFormatPattern(dateStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return ZonedDateTime.parse(dateStr, formatter);
        } catch (DateTimeException ex) {
            log.error("parseStringToZonedDateTime error: ", ex);
            return parseStringToLocalDateTime(dateStr).atZone(ZoneOffset.UTC);
        } catch (Exception ex) {
            log.error("parseStringToZonedDateTime error: ", ex);
            throw new RuntimeException();
        }
    }
}

