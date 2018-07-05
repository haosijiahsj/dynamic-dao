package com.zzz.processor.impl;

import com.google.common.base.Preconditions;
import com.zzz.annotations.BatchUpdate;
import com.zzz.processor.BaseMethodProcessor;
import com.zzz.support.QueryParam;

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
        QueryParam queryParam = new QueryParam(args, argsAnnotations);

        Preconditions.checkArgument(queryParam.onlyOneArg(), "批量操作只允许传入一个参数！");
        Preconditions.checkArgument(queryParam.firstArg() instanceof List, "批量操作只允许传入一个List参数！");

        if (queryParam.isNamed()) {
            List<Map<String, ?>> mapList = (List<Map<String, ?>>) queryParam.firstArg();

            Map<String, ?>[] maps = mapList.toArray(new Map[0]);
            return namedParameterJdbcTemplate.batchUpdate(annotation.value(), maps);
        }

        List<Object[]> objects = (List<Object[]>) queryParam.firstArg();
        return jdbcTemplate.batchUpdate(annotation.value(), objects);
    }

}
