package com.husj.dynamicdao.reflect;

import com.husj.dynamicdao.annotations.mapping.EnumType;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.reflect.definition.ColumnDefinition;
import com.husj.dynamicdao.reflect.definition.TableDefinition;
import com.husj.dynamicdao.support.AttributeConverter;
import com.husj.dynamicdao.support.DefaultAttributeConverter;
import com.husj.dynamicdao.utils.DateTimeUtils;
import com.husj.dynamicdao.utils.NumberUtils;
import com.husj.dynamicdao.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
     * 获取列值
     * @param arg
     * @param tableDefinition
     * @return
     */
    public static Map<String, Object> getColumnValue(Object arg, TableDefinition tableDefinition) {
        List<ColumnDefinition> columnDefinitions = tableDefinition.getColumnDefinitions();
        Map<String, Object> map = new LinkedHashMap<>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.isPrimaryKey()) {
                continue;
            }
            Field field = columnDefinition.getField();
            try {
                Object columnValue = getObjectValue(arg, field);
                if (columnValue != null) {
                    Class<? extends AttributeConverter> converter = columnDefinition.getConverter();
                    EnumType enumType = columnDefinition.getEnumType();
                    // 处理转换器注解
                    if (converter != null && !converter.equals(DefaultAttributeConverter.class)) {
                        AttributeConverter attributeConverter = converter.newInstance();
                        columnValue = attributeConverter.convertToDatabaseColumn(columnValue);
                    } else if (enumType != null && !EnumType.NONE.equals(enumType)) {
                        Assert.isTrue(field.getType().isEnum(), String.format("字段：[%s]不是枚举，无需定义EnumType !", field.getName()));
                        Enum enumValue = (Enum) columnValue;
                        columnValue = EnumType.STRING.equals(enumType) ? enumValue.name() : enumValue.ordinal();
                    } else if (LocalDateTime.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertLocalDateTime2Timestamp((LocalDateTime) columnValue);
                    } else if (LocalDate.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertLocalDate2SqlDate((LocalDate) columnValue);
                    } else if (Date.class.equals(field.getType())) {
                        columnValue = DateTimeUtils.convertDate2Timestamp((Date) columnValue);
                    }
                }
                map.put(columnDefinition.getColumnName(), columnValue);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new DynamicDaoException(String.format("Can't get [%s] value !", field.getName()), e);
            }
        }

        return map;
    }

    /**
     * 获取值
     * @param arg
     * @param field
     * @return
     */
    public static Object getObjectValue(Object arg, Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }

        try {

            return field.get(arg);
        } catch (Exception e) {
            throw new DynamicDaoException(String.format("Can't get field: [%s] value !", field), e);
        }
    }

    /**
     * 设置值
     * @param arg
     * @param targetValue
     * @param field
     */
    public static void setObjectValue(Object arg, Object targetValue, Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(arg, targetValue);
        } catch (IllegalAccessException e) {
            throw new DynamicDaoException(String.format("Can't set field: [%s] value !", field), e);
        }
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
        List<ColumnDefinition> columnDefinitions = MappingUtils.getColumnDefinitionsByClass(targetClass);
        List<Object> objects = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            Object object;
            try {
                object = targetClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new DynamicDaoException(String.format("Can't instance class: [%s] !", targetClass.getName()), e);
            }

            for (ColumnDefinition columnDefinition : columnDefinitions) {
                Field field = columnDefinition.getField();
                String columnName = columnDefinition.getColumnName();

                Object value = map.get(ignoreCase ? columnName.toUpperCase() : columnName);
                if (value == null) {
                    continue;
                }

                Object targetValue;
                Class<? extends AttributeConverter> converter = columnDefinition.getConverter();
                EnumType enumType = columnDefinition.getEnumType();
                if (converter != null && !DefaultAttributeConverter.class.equals(converter)) {
                    try {
                        AttributeConverter attributeConverter = converter.newInstance();
                        targetValue = attributeConverter.convertToEntityAttribute(value);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new DynamicDaoException("Convert column to entity error!", e);
                    }
                } else if (enumType != null && !EnumType.NONE.equals(enumType)) {
                    Assert.isTrue(field.getType().isEnum(), String.format("字段：[%s]不是枚举，无需定义EnumType !", field.getName()));
                    if (EnumType.STRING.equals(enumType)) {
                        targetValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString());
                    } else {
                        Object[] enumConstants = field.getType().getEnumConstants();
                        int ordinal = Integer.parseInt(value.toString());
                        targetValue = enumConstants[ordinal];
                    }
                } else {
                    // 若map中该fieldName的值不为空，则转换成字段中需要的数据类型
                    targetValue = convertObjectToTarget(field.getType(), value);
                }

                if (targetValue == null) {
                    continue;
                }

                setObjectValue(object, targetValue, field);
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
        List<Map<String, Object>> result = mapList.stream()
                .map(m -> {
                    Map<String, Object> resultMap = new HashMap<>(m.size());
                    m.keySet().forEach(k -> {
                        // 将所有数据列名转换为大写
                        String key = ignoreCase ? k.toUpperCase() : k;
                        // 不为空才进行替换
                        key = StringUtils.isNotEmpty(ignoreString) ? key.replace(ignoreString, "") : key;

                        resultMap.put(key, m.get(k));
                    });
                    return resultMap;
                })
                .collect(Collectors.toList());

        return rowMapping(result, targetClass, ignoreCase);
    }

}
