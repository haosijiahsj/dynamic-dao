package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.Get;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.sql.BaseSqlGenerator;
import com.husj.dynamicdao.sql.GetSqlGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.CollectionUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
public class GetMethodProcessor extends BaseMethodProcessor<Get> {

    @Override
    public Object process() throws Exception {
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        BaseSqlGenerator<Get> sqlGenerator = new GetSqlGenerator(method, annotation, queryParam);
        SqlParam sqlParam = sqlGenerator.generateSql();

        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sqlParam.getSql(), sqlParam.getArgs());

        if (CollectionUtils.isEmpty(mapList)) {
            return null;
        }

        Assert.isTrue(mapList.size() == 1, "Get query return more than 1 return value !");

        return ReflectUtils.rowMapping(mapList, method.getReturnType(), configuration.isIgnoreCase()).get(0);
    }

}
