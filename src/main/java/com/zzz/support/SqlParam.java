package com.zzz.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Map<String, Object> paramMap;

    public static SqlParam of(String sql, Object[] args, Map<String, Object> paramMap) {
        return new SqlParam(sql, args, paramMap);
    }

}
