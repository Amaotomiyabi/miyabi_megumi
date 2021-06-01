package com.megumi.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 2021/2/22
 *
 * @author miyabi
 * @since 1.0
 */
public class TimeUtil {
    private static final DateTimeFormatter defaultDTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter defaultTFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter defaultDFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter customFormatter;

    public TimeUtil(String customFormatter) {
        this.customFormatter = DateTimeFormatter.ofPattern(customFormatter);
    }

    public static String now() {
        return LocalDateTime.now().format(defaultDFormatter);
    }

    public static String day() {
        return LocalDate.now().format(defaultDFormatter);
    }

    public static String time() {
        return LocalTime.now().format(defaultTFormatter);
    }

    public DateTimeFormatter getCustomFormatter() {
        return customFormatter;
    }

    public void setCustomFormatter(DateTimeFormatter customFormatter) {
        this.customFormatter = customFormatter;
    }

    public String customTime() {
        return LocalDateTime.now().format(customFormatter);
    }
}
