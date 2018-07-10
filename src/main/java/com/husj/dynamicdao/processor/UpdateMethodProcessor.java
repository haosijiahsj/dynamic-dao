package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.Update;
import com.husj.dynamicdao.sql.BaseSqlGenerator;
import com.husj.dynamicdao.sql.UpdateSqlGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
public class UpdateMethodProcessor extends BaseMethodProcessor<Update> {

    @Override
    public Object process() throws Exception {
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        BaseSqlGenerator<Update> sqlGenerator = new UpdateSqlGenerator(method, annotation, queryParam);
        SqlParam sqlParam = sqlGenerator.generateSql();

        if (queryParam.isNamed()) {
            return namedParameterJdbcTemplate.update(sqlParam.getSql(), sqlParam.getParamMap());
        }

        return jdbcTemplate.update(sqlParam.getSql(), sqlParam.getArgs());
    }

}
