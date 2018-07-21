package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Get;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
public class GetSqlGenerator extends BaseSqlGenerator<Get> {

    private static final String SELECT = "SELECT * FROM %s WHERE %s = ?";

    public GetSqlGenerator(Method method, Get annotation, QueryParam queryParam) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
    }

    @Override
    public SqlParam generateSql() throws Exception {
        Assert.isTrue(queryParam.onlyOneArg(), "Use Get annotation, just support one argument !");
        Class<?> returnType = method.getReturnType();

        String tableName = ReflectUtils.getTableName(returnType);
        String idName = ReflectUtils.getIdName(returnType);

        SqlParam sqlParam = new SqlParam();
        sqlParam.setSql(String.format(SELECT, tableName, idName));
        sqlParam.setArgs(queryParam.getArgs());

        log.debug("SQL statement [{}]", sqlParam.getSql());
        log.debug("SQL arguments {}", Arrays.toString(sqlParam.getArgs()));

        return sqlParam;
    }

}
