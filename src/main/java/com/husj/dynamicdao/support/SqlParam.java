package com.husj.dynamicdao.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlParam {

    private String sql;
    private Object[] args;
    private List<Map<String, Object>> argMaps;

    public static SqlParam of(String sql, Object[] args) {
        return new SqlParam(sql, args, null);
    }

    public static SqlParam of(String sql, Object[] args, List<Map<String, Object>> argMaps) {
        return new SqlParam(sql, args, argMaps);
    }

}
