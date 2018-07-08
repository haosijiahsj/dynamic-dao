package com.zzz.processor;

import com.zzz.annotations.Update;
import com.zzz.sql.BaseSqlGenerator;
import com.zzz.sql.UpdateSqlGenerator;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;

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

        // 在Update注解上传入了sql
        if (!"".equals(annotation.value())) {
            if (queryParam.isNamed()) {
                return namedParameterJdbcTemplate.update(sqlParam.getSql(), sqlParam.getParamMap());
            } else {
                return jdbcTemplate.update(sqlParam.getSql(), sqlParam.getArgs());
            }
        }

        return jdbcTemplate.update(sqlParam.getSql(), sqlParam.getArgs());
    }

}
