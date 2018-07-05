package com.zzz.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author 胡胜钧
 * @date 7/5 0005.
 */
public class TimeUtils {

    private TimeUtils() {}

    public static LocalDateTime convertDate2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime convertTimestamp2LocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDate convertSqlDate2LocalDate(java.sql.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date convertLocalDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Timestamp convertLocalDateTime2Timestamp(LocalDateTime localDateTime) {
        return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static java.sql.Date convertLocalDate2SqlDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static Timestamp convertDate2Timestamp(Date date) {
        return new Timestamp(date.getTime());
    }

}
