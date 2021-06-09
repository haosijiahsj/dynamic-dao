package com.husj.dynamicdao.reflect.definition;

import com.husj.dynamicdao.annotations.mapping.EnumType;
import com.husj.dynamicdao.annotations.mapping.IdType;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * ColumnDefinition
 *
 * @author shengjun.hu
 * @date 2021/6/8
 */
@Data
public class ColumnDefinition {

    private String fieldName;
    private Field field;
    private String columnName;
    private boolean primaryKey = false;
    private IdType idType;
    private Class<?> converter;
    private EnumType enumType;

}
