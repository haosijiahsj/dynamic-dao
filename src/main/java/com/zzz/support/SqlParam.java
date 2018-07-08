package com.zzz.support;

import lombok.Data;

import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
@Data
public class SqlParam {

    private String sql;
    private Object[] args;
    private Map<String, Object> paramMap;

    public SqlParam() {}

    private SqlParam(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    private SqlParam(String sql, Map<String, Object> paramMap) {
        this.sql = sql;
        this.paramMap = paramMap;
    }

    private SqlParam(String sql, Object[] args, Map<String, Object> paramMap) {
        this.sql = sql;
        this.args = args;
        this.paramMap = paramMap;
    }

    public static SqlParam of(String sql, Object[] args, Map<String, Object> paramMap) {
        return new SqlParam(sql, args, paramMap);
    }

    public static SqlParam of(String sql, Object[] args) {
        return new SqlParam(sql, args);
    }

    public static SqlParam of(String sql, Map<String, Object> paramMap) {
        return new SqlParam(sql, paramMap);
    }

}
