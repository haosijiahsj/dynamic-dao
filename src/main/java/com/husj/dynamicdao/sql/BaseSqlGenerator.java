package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public abstract class BaseSqlGenerator<T> {

    protected static final Logger log = LoggerFactory.getLogger(BaseSqlGenerator.class);

    protected Method method;
    protected T annotation;
    protected QueryParam queryParam;

    public abstract SqlParam generateSql() throws Exception;

    public SqlParam generatePageSql(String sql) {
        return null;
    }

    public SqlParam generateCountSql(String sql) {
        return null;
    }

}
