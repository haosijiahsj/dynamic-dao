package com.husj.dynamicdao.convert;

import com.husj.dynamicdao.model.Status;
import com.husj.dynamicdao.support.AttributeConverter;

/**
 * StatusConverter
 *
 * @author shengjun.hu
 * @date 2021/6/8
 */
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute.name();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Status.valueOf(dbData);
    }

}
