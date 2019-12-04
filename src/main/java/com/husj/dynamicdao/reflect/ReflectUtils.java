package com.husj.dynamicdao.reflect;

import com.husj.dynamicdao.annotations.support.Convert;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.support.AttributeConverter;
import com.husj.dynamicdao.utils.StringUtils;
import com.husj.dynamicdao.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import com.husj.dynamicdao.utils.NumberUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
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
                    // 处理转换器注解
                    Convert convertAnno = field.getAnnotation(Convert.class);
                    Enumerated enumeratedAnno = field.getAnnotation(Enumerated.class);
                    if (convertAnno != null) {
                        Class converter = convertAnno.converter();
                        AttributeConverter attributeConverter = (AttributeConverter) converter.newInstance();
                        columnValue = attributeConverter.convertToDatabaseColumn(columnValue);
                    } else if (enumeratedAnno != null && field.getType().isEnum()) {
                        Enum enumValue = (Enum) columnValue;
                        columnValue = EnumType.STRING.equals(enumeratedAnno.value()) ? enumValue.name() : enumValue.ordinal();
                    } else if (LocalDateTime.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertLocalDateTime2Timestamp((LocalDateTime) columnValue);
                    } else if (LocalDate.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertLocalDate2SqlDate((LocalDate) columnValue);
                    } else if (Date.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertDate2Timestamp((Date) columnValue);
                    }
                }
                field.setAccessible(false);
                map.put(columnName, columnValue);
            } catch (IllegalAccessException | InstantiationException e) {
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
     * 转换列值到对应类型
     * @param targetClass
     * @param value
     * @return
     */
    public static Object convertObjectToTarget(Class targetClass, Object value) {
        Class srcClass = value.getClass();

        // 字段类型与列类型相同时直接返回
        if (targetClass.equals(srcClass)) {
            return value;
        }

        // 字段类型为字符串类型
        String strValue = value.toString();
        if (String.class.equals(targetClass)) {
            return strValue;
        }

        // 字段类型为数字类型
        if (NumberUtils.isNumberType(targetClass)) {
            return NumberUtils.parseNumber(strValue, targetClass);
        }

        // 字段类型为日期类型
        if (DateTimeUtils.isDateType(targetClass)) {
            return DateTimeUtils.convertDateToTargetType((Date) value, targetClass);
        }

        // 字段类型为枚举类型
        if (targetClass.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetClass, strValue);
        }

        Object targetValue = null;
        // 字段类型为bool类型
        if (Boolean.class.equals(targetClass) || boolean.class.equals(targetClass)) {
            if (Arrays.asList(Boolean.TRUE.toString(), Boolean.FALSE.toString()).contains(strValue.toLowerCase())) {
                targetValue = Boolean.parseBoolean(strValue);
            } else {
                if (!Arrays.asList(BigInteger.ONE.toString(), BigInteger.ZERO.toString()).contains(strValue)) {
                    throw new DynamicDaoException(String.format("Can't convert column value to 'boolean', 'value: %s' is not [true, false or 0, 1] !", value));
                }
                // 此处大于0的所有都将会转换为true
                targetValue = BigInteger.ONE.toString().equals(strValue);
            }
        }
        // 字段类型为char类型
        else if (Character.class.equals(targetClass) || char.class.equals(targetClass)) {
            if (strValue.length() > 1) {
                throw new DynamicDaoException(String.format("Can't convert column value to 'char', 'value: %s' is to long !", value));
            }
            targetValue = strValue.charAt(0);
        }

        return targetValue;
    }

    /**
     * 映射
     *
     * @param mapList
     * @param targetClass
     * @return
     */
    public static List<Object> rowMapping(List<Map<String, Object>> mapList, Class targetClass, boolean ignoreCase) {
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
                Object value = map.get(ignoreCase ? fieldName.toUpperCase() : fieldName);
                if (value == null) {
                    continue;
                }

                Object targetValue;
                Convert convertAnno = field.getAnnotation(Convert.class);
                Enumerated enumeratedAnno = field.getAnnotation(Enumerated.class);
                if (convertAnno != null) {
                    // 处理Convert注解
                    Class converter = convertAnno.converter();
                    try {
                        AttributeConverter attributeConverter = (AttributeConverter) converter.newInstance();
                        targetValue = attributeConverter.convertToEntityAttribute(value);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new DynamicDaoException("Convert column to entity error!", e);
                    }
                } else if (enumeratedAnno != null && field.getType().isEnum()) {
                    // 处理枚举，仅在有Enumerated注解才进行处理
                    targetValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString());
                } else {
                    // 若map中该fieldName的值不为空，则转换成字段中需要的数据类型
                    targetValue = convertObjectToTarget(field.getType(), value);
                }

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
    public static List<Object> rowMapping(List<Map<String, Object>> mapList, Class targetClass, String ignoreString, boolean ignoreCase) {
        mapList = mapList.stream().map(m -> {
            Map<String, Object> resultMap = new HashMap<>(m.size());
            m.keySet().forEach(k -> {
                // 将所有数据列名转换为大写
                String key = ignoreCase ? k.toUpperCase() : k;
                // 不为空才进行替换
                key = StringUtils.isNotEmpty(ignoreString) ? key.replace(ignoreString, "") : key;

                resultMap.put(key, m.get(k));
            });
            return resultMap;
        }).collect(Collectors.toList());

        return rowMapping(mapList, targetClass, ignoreCase);
    }

}
