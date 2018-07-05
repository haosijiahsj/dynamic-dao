package com.zzz.reflect;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zzz.utils.TimeUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
public class ReflectUtils {

    private ReflectUtils() {}

    /**
     * 获取值
     * @param arg
     * @return
     */
    public static Map<String, Object> getColumnValue(Object arg) {
        Field[] fields = arg.getClass().getDeclaredFields();
        Map<String, Object> map = Maps.newHashMap();
        for (Field field : fields) {
            Id idAnno = field.getAnnotation(Id.class);
            // 不扫描@Id列
            if (idAnno != null) {
                continue;
            }
            Column columnAnno = field.getAnnotation(Column.class);
            if (columnAnno == null) {
                continue;
            }

            String columnName = "".equals(columnAnno.name()) ? field.getName() : columnAnno.name();
            try {
                field.setAccessible(true);
                Object columnValue = field.get(arg);
                if (columnValue != null) {
                    // 处理枚举情况
                    if (field.getType().isEnum()) {
                        Enum enumValue = (Enum) columnValue;
                        Enumerated enumeratedAnno = field.getAnnotation(Enumerated.class);
                        if (enumeratedAnno != null) {
                            columnValue = EnumType.STRING.equals(enumeratedAnno.value()) ? enumValue.name() : enumValue.ordinal();
                        } else {
                            columnValue = enumValue.name();
                        }
                    } else if (LocalDateTime.class.equals(field.getType())) {
                        columnValue = TimeUtils.convertLocalDateTime2Timestamp((LocalDateTime) columnValue);
                    } else if (LocalDate.class.equals(field.getType())) {
                        columnValue = TimeUtils.convertLocalDate2SqlDate((LocalDate) columnValue);
                    } else if (Date.class.equals(field.getType())) {
                        columnValue = TimeUtils.convertDate2Timestamp((Date) columnValue);
                    }
                }
                field.setAccessible(false);
                map.put(columnName, columnValue);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(String.format("获取字段[%s]的值时出现异常！", field.getName()), e);
            }
        }

        return map;
    }

    public static Map<String, Object> getIdValue(Object arg) {
        Field[] fields = arg.getClass().getDeclaredFields();
        Map<String, Object> map = Maps.newHashMap();
        for (Field field : fields) {
            Id idAnno = field.getAnnotation(Id.class);
            if (idAnno == null) {
                continue;
            }
            Column columnAnno = field.getAnnotation(Column.class);
            String columnName = field.getName();
            if (columnAnno != null && !"".equals(columnAnno.name())) {
                columnName = columnAnno.name();
            }

            try {
                field.setAccessible(true);
                Object columnValue = field.get(arg);
                field.setAccessible(false);
                map.put(columnName, columnValue);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(String.format("获取字段[%s]的值时出现异常！", field.getName()), e);
            }
        }

        Preconditions.checkArgument(map.size() == 1, String.format("类[%s]没有或有多个id列！", arg.getClass().getName()));

        return map;
    }

    /**
     * 获取表名
     * @param arg
     * @return
     */
    public static String getTableName(Object arg) {
        Entity entityAnno = arg.getClass().getAnnotation(Entity.class);
        Table tableAnno = arg.getClass().getAnnotation(Table.class);
        Preconditions.checkArgument(entityAnno != null, String.format("类[%s]必须要有@Entity注解！", arg.getClass().getName()));
        Preconditions.checkArgument(tableAnno != null, String.format("类[%s]必须要有@Table注解！", arg.getClass().getName()));

        return "".equals(tableAnno.name()) ? arg.getClass().getSimpleName() : tableAnno.name();
    }

}
