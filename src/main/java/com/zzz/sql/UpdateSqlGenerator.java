package com.zzz.sql;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zzz.annotations.Update;
import com.zzz.reflect.ReflectUtils;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class UpdateSqlGenerator extends BaseSqlGenerator<Update> {

    private static final String UPDATE = "UPDATE ";

    public UpdateSqlGenerator(Method method, Update annotation, QueryParam queryParam) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
    }

    @Override
    public SqlParam generateSql() {
        if (!"".equals(annotation.value())) {
            return SqlParam.of(annotation.value(), queryParam.getArgs(), queryParam.getParamMap());
        }

        String sql = UPDATE + ReflectUtils.getTableName(queryParam.firstArg()) + " SET ";

        Map<String, Object> idMap = ReflectUtils.getIdValue(queryParam.firstArg());
        Map<String, Object> columnMap = ReflectUtils.getColumnValue(queryParam.firstArg());

        List<String> propertyStrs = Lists.newArrayList();
        columnMap.keySet().forEach(k -> propertyStrs.add("`" + k + "` = ?"));

        List<String> idStrs = Lists.newArrayList();
        idMap.keySet().forEach(k -> idStrs.add("`" + k + "` = ?"));

        sql += Joiner.on(", ").join(propertyStrs);
        sql += " WHERE ";
        sql += Joiner.on(", ").join(idStrs);

        log.debug("生成的sql语句为：[{}]", sql);

        Object[] newArgs = new Object[idMap.size() + columnMap.size()];

        int i = 0;
        for (String key : columnMap.keySet()) {
            newArgs[i] = columnMap.get(key);
            i++;
        }
        for (String key : idMap.keySet()) {
            newArgs[i] = idMap.get(key);
            i++;
        }

        columnMap.putAll(idMap);

        return SqlParam.of(sql, newArgs, columnMap);
    }

}
