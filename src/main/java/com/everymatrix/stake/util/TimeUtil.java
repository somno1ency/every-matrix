package com.everymatrix.stake.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author mackay.zhou
 * created at 2024/12/26
 */
public class TimeUtil {

    private TimeUtil() {}

    public static boolean isBeforeToday(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        return date.isBefore(today);
    }
}
