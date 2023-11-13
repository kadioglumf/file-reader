package com.kadioglumf.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateUtils {

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{1,2}.\\d{1,2}.\\d{4}$", "dd.MM.yyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    }};

    private static final Map<String, String> DATE_WITH_TIME_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{14}$", "yyyyMMddHHmmss");
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{6}$", "yyyy-MM-dd HH:mm:ss.SSSSSS");
    }};

    private static final Map<String, String> TIME_FORMAT_REGEXPS = new HashMap<>() {{
        put("^\\d{1,2}:\\d{2}$", "HH:mm");
    }};

    private static final Map<String, String> DATE_ALL_FORMAT_REGEXPS = new HashMap<>() {{
        putAll(DATE_FORMAT_REGEXPS);
        putAll(DATE_WITH_TIME_FORMAT_REGEXPS);
    }};

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     */
    public static String determineDateFormatPattern(String dateString) {
        for (String regexp : DATE_ALL_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_ALL_FORMAT_REGEXPS.get(regexp);
            }
        }
        throw new RuntimeException("");
    }

    public static String determineTimeFormatPattern(String dateString) {
        for (String regexp : TIME_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return TIME_FORMAT_REGEXPS.get(regexp);
            }
        }
        throw new RuntimeException("");
    }


    public static LocalDate parseStringToLocalDate(String dateStr) {
        try {
            String pattern = determineDateFormatPattern(dateStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    public static LocalDateTime parseStringToLocalDateTime(String dateTimeStr) {
        try {
            String pattern = determineDateFormatPattern(dateTimeStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    public static LocalTime parseStringToLocalTime(String dateTimeStr) {
        try {
            String pattern = determineTimeFormatPattern(dateTimeStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalTime.parse(dateTimeStr, formatter);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    public static Date parseStringToDate(String date) {
        try {
            String pattern = determineDateFormatPattern(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    public static Instant parseStringToInstant(String date) {
        try {
            String pattern = determineDateFormatPattern(date);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return Instant.from(formatter.parse(date));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    public static Object parseStringToObjectByFormat(String dateStr) {
        String pattern = null;
        for (String regexp : DATE_WITH_TIME_FORMAT_REGEXPS.keySet()) {
            if (dateStr.toLowerCase().matches(regexp)) {
                pattern = DATE_ALL_FORMAT_REGEXPS.get(regexp);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(dateStr, formatter);
            }
        }
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateStr.toLowerCase().matches(regexp)) {
                pattern = DATE_FORMAT_REGEXPS.get(regexp);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateStr, formatter);
            }
        }
        throw new RuntimeException("");
    }

    public static String parseLocalDateToString(LocalDate dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateStr.format(formatter);
    }

    public static String LocalDateToStringWithFormat(LocalDate date, String pattern) {
        if (date == null || StringUtils.isBlank(pattern)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(date);
    }

    /**
     * Generates Date list with zero time from given Date to given day before
     * <pre>
     * Example;
     *   from : 2022-08-25T12:34:22.204+0300
     *   toDayBefore: 3
     *
     *   result:
     *      [
     *          "2022-08-23T00:00:00.000+0300",
     *          "2022-08-24T00:00:00.000+0300",
     *          "2022-08-25T00:00:00.000+0300"
     *      ]
     * </pre>
     * @param from The Date from will list start
     * @param toDayBefore The number of days before from given date start
     * @return List of Dates With Zero Time
     */
    public static List<Date> generateDateList(Date from, int toDayBefore) {
        List<Date> dates = new ArrayList<>();

        for (int i = toDayBefore - 1; i >= 0; i--) {
            dates.add(generateDateBeforeFromDate(removeTimeFromDate(from), i));
        }

        return dates;
    }

    /**
     * Removes time from given Date
     * @param date The Date will remove time
     * @return Date with time zero like XXXX-XX-XXT00:00:00.000+00:00
     */
    public static Date removeTimeFromDate(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds day to given date
     * @param date The Date to add more days
     * @param days The number of days will add to given Date
     * @return New Date with added days
     */
    public static synchronized Date generateDateAfterGivenDate(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * Adds day to given date
     * @param date The Date to add more days
     * @param days The number of days will add to given Date
     * @return New Date with added days
     */
    public static Date generateDateBeforeFromDate(Date date, int days) {
        return new Date(date.getTime() - days * (24 * 60 * 60 * 1000));
    }

    public static synchronized Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }
}

