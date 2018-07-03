package com.zzz.reflect;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
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
                field.setAccessible(false);
                map.put(columnName, columnValue);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(String.format("在保存时获取字段[%s]的值时出现异常！", field.getName()), e);
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
