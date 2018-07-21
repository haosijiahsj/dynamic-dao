package com.husj.dynamicdao.reflect;

import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.utils.StringUtils;
import com.husj.dynamicdao.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
        Map<String, Object> map = new HashMap<>();
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
                throw new DynamicDaoException(String.format("Can't get [%s] value !", field.getName()), e);
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
        Map<String, Object> map = new HashMap<>();
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
                throw new DynamicDaoException(String.format("Can't get [%s] value !", field.getName()), e);
            }
        }

        Assert.isTrue(map.size() == 1, String.format("Class [%s] allow only one 'id' column !", arg.getClass().getName()));

        return map;
    }

    /**
     * 获取有@Id注解的字段
     * @param clazz
     * @return
     */
    private static Field getIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Id idAnno = field.getAnnotation(Id.class);
            if (idAnno != null) {
                return field;
            }
        }

        return null;
    }

    /**
     * 获取id列名称
     * @param clazz
     * @return
     */
    public static String getIdName(Class<?> clazz) {
        Field field = getIdField(clazz);
        if (field == null) {
            throw new DynamicDaoException(String.format("Can't find id field in [%s]!", clazz));
        }

        String idName = field.getName();
        Column columnAnno = field.getAnnotation(Column.class);
        if (columnAnno != null && !"".equals(columnAnno.name())) {
            idName = columnAnno.name();
        }

        return idName;
    }

    /**
     * 获取表名
     * @param arg
     * @return
     */
    public static String getTableName(Object arg) {
        return getTableName(arg.getClass());
    }

    /**
     * 获取表名
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        Entity entityAnno = clazz.getAnnotation(Entity.class);
        Table tableAnno = clazz.getAnnotation(Table.class);

        Assert.isTrue(entityAnno != null, String.format("Class [%s] must have 'Entity' annotation !", clazz.getName()));
        Assert.isTrue(tableAnno != null, String.format("Class [%s] must have 'Table' annotation !", clazz.getName()));

        return "".equals(tableAnno.name()) ? clazz.getSimpleName() : tableAnno.name();
    }

    /**
     * 将object转换到需要的类型
     * @param fieldType
     * @param value
     * @return
     */
    private static Object convertObject4Target(Class fieldType, Object value) {
        Class columnType = value.getClass();
        Object targetValue = null;
        // 对象类型与列类型相同
        if (fieldType.equals(columnType)) {
            targetValue = value;
        } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
            targetValue = Byte.valueOf(value.toString());
        } else if (Boolean.class.equals(columnType) || boolean.class.equals(columnType)) {
            if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                targetValue = value;
            } else {
                if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
                    targetValue = (Boolean) value ? (short) 1 : (short) 0;
                } else {
                    targetValue = (Boolean) value ? (byte) 1 : (byte) 0;
                }
            }
        } else if (BigDecimal.class.equals(fieldType)) {
            targetValue = new BigDecimal(value.toString());
        } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            targetValue = Integer.valueOf(value.toString());
        } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            targetValue = Long.valueOf(value.toString());
        } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
            targetValue = Short.valueOf(value.toString());
        } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
            targetValue = Float.valueOf(value.toString());
        } else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
            targetValue = Double.valueOf(value.toString());
        } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            if (Boolean.class.equals(value) || boolean.class.equals(value)) {
                targetValue = value;
            } else {
                targetValue = "1".equals(value.toString());
            }
        } else if (Timestamp.class.equals(columnType)) {
            // 支持java8日期
            if (LocalDateTime.class.equals(fieldType)) {
                targetValue = TimeUtils.convertTimestamp2LocalDateTime((Timestamp) value);
            } else {
                targetValue = new Date(((Timestamp) value).getTime());
            }
        } else if (java.sql.Date.class.equals(columnType)) {
            // 支持java8日期
            if (LocalDate.class.equals(fieldType)) {
                targetValue = TimeUtils.convertSqlDate2LocalDate((java.sql.Date) value);
            } else {
                targetValue = new Date(((java.sql.Date) value).getTime());
            }
        } else if (fieldType.isEnum()) {
            // 支持枚举
            targetValue = Enum.valueOf((Class<Enum>) fieldType, value.toString());
        } else {
            log.warn("Type doesn't match ! type of column: [{}], type of filed: [{}] , value: [{}]", columnType, fieldType, value);
        }

        return targetValue;
    }

    /**
     * 映射
     *
     * @param mapList
     * @param targetClass
     * @return
     * @throws Exception
     */
    public static List<Object> rowMapping(List<Map<String, Object>> mapList, Class targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        List<Object> objects = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            Object object;
            try {
                object = targetClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new DynamicDaoException(String.format("Can't instance class: [%s] !", targetClass.getName()), e);
            }
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

                // 若map中该fieldName的值不为空，则转换成字段中需要的数据类型
                Object targetValue = convertObject4Target(field.getType(), value);
                if (targetValue == null) {
                    continue;
                }

                field.setAccessible(true);
                try {
                    field.set(object, targetValue);
                } catch (IllegalAccessException e) {
                    throw new DynamicDaoException(String.format("Can't set field: [%s] value !", field), e);
                }
                field.setAccessible(false);
            }

            objects.add(object);
        }

        return objects;
    }

    /**
     * mapList中key的ignoreString替换为""
     * @param mapList
     * @param targetClass
     * @param ignoreString
     * @return
     * @throws Exception
     */
    public static List<Object> rowMapping(List<Map<String, Object>> mapList, Class targetClass, String ignoreString) {
        mapList = mapList.stream().map(m -> {
            Map<String, Object> resultMap = new HashMap<>();
            m.keySet().forEach(k -> {
                // 将所有数据列名转换为大写
                String key = k.toUpperCase();
                // 不为空才进行替换
                key = StringUtils.isNotEmpty(ignoreString) ? key.replace(ignoreString, "") : key;

                resultMap.put(key, m.get(k));
            });
            return resultMap;
        }).collect(Collectors.toList());

        return rowMapping(mapList, targetClass);
    }

}
