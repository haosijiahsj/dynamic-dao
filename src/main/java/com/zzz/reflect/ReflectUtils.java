package com.zzz.reflect;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzz.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@Slf4j
@SuppressWarnings("unchecked")
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

    /**
     * 获取@Id列的值
     * @param arg
     * @return
     */
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

    /**
     * 映射
     *
     * @param mapList
     * @param targetClass
     * @return
     * @throws Exception
     */
    public static List<Object> rowMapping(List<Map<String, Object>> mapList, Class targetClass) throws Exception {
        // 将查询结果key转换为大写
        List<Map<String, Object>> resultList = mapList.stream().map(map -> {
            Map<String, Object> resultMap = Maps.newHashMap();
            map.keySet().forEach(k -> resultMap.put(k.toUpperCase(), map.get(k)));
            return resultMap;
        }).collect(Collectors.toList());

        Field[] fields = targetClass.getDeclaredFields();
        List<Object> objects = Lists.newArrayList();

        for (Map<String, Object> map : resultList) {
            Object object = targetClass.newInstance();
            for (Field field : fields) {
                String fieldName = field.getName();
                Column columnAnno = field.getAnnotation(Column.class);
                if (columnAnno != null && !"".equals(columnAnno.name())) {
                    fieldName = columnAnno.name();
                }
                Object value = map.get(fieldName.toUpperCase());
                if (value == null) {
                    continue;
                }

                Class<?> fieldType = field.getType();
                Class<?> columnType = value.getClass();

                field.setAccessible(true);
                // 对象类型与列类型相同
                if (fieldType.equals(columnType)) {
                    field.set(object, value);
                } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
                    field.set(object, Byte.valueOf(value.toString()));
                } else if (Boolean.class.equals(columnType) || boolean.class.equals(columnType)) {
                    if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                        field.set(object, value);
                    } else {
                        if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
                            field.set(object, (Boolean) value ? (short) 1 : (short) 0);
                        } else {
                            field.set(object, (Boolean) value ? (byte) 1 : (byte) 0);
                        }
                    }
                } else if (BigDecimal.class.equals(fieldType)) {
                    field.set(object, new BigDecimal(value.toString()));
                } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                    field.set(object, Integer.valueOf(value.toString()));
                } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
                    field.set(object, Long.valueOf(value.toString()));
                } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
                    field.set(object, Short.valueOf(value.toString()));
                } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
                    field.set(object, Float.valueOf(value.toString()));
                } else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
                    field.set(object, Double.valueOf(value.toString()));
                } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                    if (Boolean.class.equals(value) || boolean.class.equals(value)) {
                        field.set(object, value);
                    } else {
                        field.set(object, "1".equals(value.toString()));
                    }
                } else if (Timestamp.class.equals(columnType)) {
                    // 支持java8日期
                    if (LocalDateTime.class.equals(fieldType)) {
                        field.set(object, TimeUtils.convertTimestamp2LocalDateTime((Timestamp) value));
                    } else {
                        field.set(object, new Date(((Timestamp) value).getTime()));
                    }
                } else if (java.sql.Date.class.equals(columnType)) {
                    // 支持java8日期
                    if (LocalDate.class.equals(fieldType)) {
                        field.set(object, TimeUtils.convertSqlDate2LocalDate((java.sql.Date) value));
                    } else {
                        field.set(object, new Date(((java.sql.Date) value).getTime()));
                    }
                } else if (fieldType.isEnum()) {
                    // 支持枚举
                    Enum<?> enums = Enum.valueOf((Class<Enum>) fieldType, value.toString());
                    field.set(object, enums);
                } else {
                    log.warn("类型不匹配 column类型 :[{}] filed类型: [{}] , value: [{}]", columnType, fieldType, value);
                }

                field.setAccessible(false);
            }

            objects.add(object);
        }

        return objects;
    }

}
