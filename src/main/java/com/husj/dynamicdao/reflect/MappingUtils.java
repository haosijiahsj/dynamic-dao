package com.husj.dynamicdao.reflect;

import com.husj.dynamicdao.annotations.mapping.Column;
import com.husj.dynamicdao.annotations.mapping.Id;
import com.husj.dynamicdao.annotations.mapping.GenerationType;
import com.husj.dynamicdao.annotations.mapping.Table;
import com.husj.dynamicdao.reflect.definition.ColumnDefinition;
import com.husj.dynamicdao.reflect.definition.TableDefinition;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MappingUtils
 *
 * @author shengjun.hu
 * @date 2021/6/8
 */
public class MappingUtils {

    private static final int DEFAULT_CAPACITY = 100;
    private static final float loadFactory = 0.75f;

    /**
     * 存放处理好的映射关系
     */
    private static final Map<Class<?>, TableDefinition> tableDefinitions
            = new LinkedHashMap<Class<?>, TableDefinition>(DEFAULT_CAPACITY, loadFactory, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > DEFAULT_CAPACITY;
        }
    };

    public static TableDefinition getTableDefinitionByClass(Class<?> clazz) {
        TableDefinition tableDefinition = tableDefinitions.get(clazz);
        if (tableDefinition == null) {
            synchronized (MappingUtils.class) {
                tableDefinition = getTableDefinition(clazz);
                tableDefinitions.put(clazz, tableDefinition);
            }
        }

        return tableDefinition;
    }

    public static List<ColumnDefinition> getColumnDefinitionsByClass(Class<?> clazz) {
        return getTableDefinitionByClass(clazz).getColumnDefinitions();
    }

    /**
     * 将所有映射关系放入定义对象中
     * @param clazz
     * @return
     */
    private static TableDefinition getTableDefinition(Class<?> clazz) {
        TableDefinition tableDefinition = new TableDefinition();

        tableDefinition.setClazz(clazz);
        tableDefinition.setClassName(clazz.getSimpleName());
        tableDefinition.setTableName(getTableName(clazz));

        List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ColumnDefinition columnDefinition = new ColumnDefinition();
            columnDefinition.setField(field);
            columnDefinition.setFieldName(field.getName());

            Id idAnno = field.getAnnotation(Id.class);
            Column columnAnno = field.getAnnotation(Column.class);
            String columnName;
            if (idAnno == null && columnAnno == null) {
                columnName = StringUtils.humpToUnderline(field.getName());
            } else {
                if (idAnno != null && columnAnno == null) {
                    // 使用uuid作为主键，则必须将主键字段类型定义为String
                    if (GenerationType.UUID.equals(idAnno.generationType())) {
                        Assert.isTrue(String.class.equals(field.getType()), "主键id使用uuid时，类型必须为String");
                    }
                    columnDefinition.setPrimaryKey(true);
                    columnDefinition.setGenerationType(idAnno.generationType());
                    columnDefinition.setGenerator(idAnno.generator());
                    columnName = StringUtils.isEmpty(idAnno.value()) ? field.getName() : idAnno.value();
                } else {
                    if (columnAnno.ignore()) {
                        continue;
                    }
                    columnDefinition.setConverter(columnAnno.converter());
                    columnDefinition.setEnumType(columnAnno.enumType());
                    columnName = StringUtils.isEmpty(columnAnno.value()) ? field.getName() : columnAnno.value();
                }
            }
            columnDefinition.setColumnName(columnName);

            columnDefinitions.add(columnDefinition);
        }
        tableDefinition.setColumnDefinitions(columnDefinitions);

        return tableDefinition;
    }

    /**
     * 获取表名
     * @param clazz
     * @return
     */
    private static String getTableName(Class<?> clazz) {
        Table tableAnno = clazz.getAnnotation(Table.class);
        // 没有该注解的将类名转为下划线作为表名
        if (tableAnno == null) {
            return StringUtils.humpToUnderline(clazz.getSimpleName());
        }

        return StringUtils.isEmpty(tableAnno.value()) ? clazz.getSimpleName() : tableAnno.value();
    }

    public static String getTableNameByClass(Class<?> clazz) {
        return getTableDefinitionByClass(clazz).getTableName();
    }

}