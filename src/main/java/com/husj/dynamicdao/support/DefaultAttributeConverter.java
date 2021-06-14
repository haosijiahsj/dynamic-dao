package com.husj.dynamicdao.support;

/**
 * DefaultAttributeConverter
 *
 * @author hushengjun
 * @date 2021-06-12-012
 */
public class DefaultAttributeConverter implements AttributeConverter<Object, Object> {

    @Override
    public Object convertToDatabaseColumn(Object attribute) {
        return attribute;
    }

    @Override
    public Object convertToEntityAttribute(Object dbData) {
        return dbData;
    }

}
