package com.husj.dynamicdao.support;

/**
 * @author hushengjun
 * @date 2019/5/15
 */
public interface AttributeConverter<X, Y> {

    /**
     * 转换到数据库列
     * @param attribute
     * @return
     */
    Y convertToDatabaseColumn(X attribute);

    /**
     * 转换到实体属性
     * @param dbData
     * @return
     */
    X convertToEntityAttribute(Y dbData);

}
