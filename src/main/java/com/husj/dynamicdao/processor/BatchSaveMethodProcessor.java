package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.BatchSave;
import com.husj.dynamicdao.sql.BaseSqlGenerator;
import com.husj.dynamicdao.sql.BatchInsertSqlGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * BatchSaveMethodProcessor
 *
 * @author shengjun.hu
 * @date 2021/6/11
 */
public class BatchSaveMethodProcessor extends BaseMethodProcessor<BatchSave> {

    @Override
    public Object process() throws Exception {
        QueryParam queryParam = new QueryParam(args, argsAnnotations);

        Assert.isTrue(queryParam.onlyOneArg(), "批量插入仅支持单个参数");
        Assert.isTrue(Collection.class.isAssignableFrom(queryParam.firstArg().getClass()), "批量插入仅支持集合类");

        BaseSqlGenerator<BatchSave> sqlGenerator = new BatchInsertSqlGenerator(method, annotation, queryParam, jdbcTemplate);
        SqlParam sqlParam = sqlGenerator.generateSql();

        Map<String, Object>[] maps = sqlParam.getArgMaps().toArray(new Map[0]);

        return namedParameterJdbcTemplate.batchUpdate(sqlParam.getSql(), maps);
    }

}
