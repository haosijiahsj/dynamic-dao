package com.zzz.processor.impl;

import com.google.common.base.Preconditions;
import com.zzz.annotations.BatchUpdate;
import com.zzz.processor.BaseMethodProcessor;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@SuppressWarnings("unchecked")
public class BatchUpdateMethodProcessor extends BaseMethodProcessor<BatchUpdate> {

    @Override
    public Object process() {
        Preconditions.checkArgument(args.length == 1, "批量操作只允许传入一个参数！");
        Preconditions.checkArgument(args[0] instanceof List, "批量操作只允许传入一个List参数！");

        if (annotation.named()) {
            List<Map<String, ?>> mapList = (List<Map<String, ?>>) args[0];

            Map<String, ?>[] maps = mapList.toArray(new Map[0]);
            return namedParameterJdbcTemplate.batchUpdate(annotation.value(), maps);
        }

        List<Object[]> objects = (List<Object[]>) args[0];
        return jdbcTemplate.batchUpdate(annotation.value(), objects);
    }

}
