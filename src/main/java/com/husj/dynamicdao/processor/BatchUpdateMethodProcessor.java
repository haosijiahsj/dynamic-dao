package com.husj.dynamicdao.processor;

import com.google.common.base.Preconditions;
import com.husj.dynamicdao.annotations.BatchUpdate;
import com.husj.dynamicdao.support.QueryParam;

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

        Preconditions.checkArgument(queryParam.onlyOneArg(), "Just support one argument !");
        Preconditions.checkArgument(queryParam.firstArg() instanceof List, "Just support one 'List' argument !");

        if (annotation.named()) {
            List<Map<String, ?>> mapList = (List<Map<String, ?>>) queryParam.firstArg();

            Map<String, ?>[] maps = mapList.toArray(new Map[0]);
            return namedParameterJdbcTemplate.batchUpdate(annotation.value(), maps);
        }

        List<Object[]> objects = (List<Object[]>) queryParam.firstArg();
        return jdbcTemplate.batchUpdate(annotation.value(), objects);
    }

}
