package com.husj.dynamicdao.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author 胡胜钧
 * @date 7/5 0005.
 */
public class DateTimeUtils {

    private DateTimeUtils() {}

    /**
     * 判断是否是日期，时间等类型
     * @param clazz
     * @return
     */
    public static boolean isDateType(Class clazz) {
        return Date.class.equals(clazz)
                || java.sql.Date.class.equals(clazz)
                || java.sql.Time.class.equals(clazz)
                || Timestamp.class.equals(clazz)
                || LocalTime.class.equals(clazz)
                || LocalDate.class.equals(clazz)
                || LocalDateTime.class.equals(clazz);
    }

    /**
     * 转换到对应日期类型
     * @param date
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertDateToTargetType(Date date, Class<T> clazz) {
        T t = null;
        if (Date.class.equals(clazz)) {
            t = (T) new Date(date.getTime());
        }
        else if (Timestamp.class.equals(clazz)) {
            t = (T) new Timestamp(date.getTime());
        }
        else if (java.sql.Date.class.equals(clazz)) {
            t = (T) new java.sql.Date(date.getTime());
        }
        else if (LocalDate.class.equals(clazz)) {
            t = (T) new java.sql.Date(date.getTime()).toLocalDate();
        }
        else if (LocalDateTime.class.equals(clazz)) {
            t = (T) new Timestamp(date.getTime()).toLocalDateTime();
        }
        else if (LocalTime.class.equals(clazz)) {
            t = (T) new java.sql.Time(date.getTime()).toLocalTime();
        }

        return t;
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
