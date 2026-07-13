package com.example.identity.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
    }

    public static boolean isAfterNow(Instant instant) {
        return instant != null && instant.isAfter(Instant.now());
    }

    public static boolean isBeforeNow(Instant instant) {
        return instant != null && instant.isBefore(Instant.now());
    }
}