package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Update;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        SqlParam sqlParam = new SqlParam();

        if (!"".equals(annotation.value())) {
            boolean flag = annotation.value().toUpperCase().startsWith("UPDATE") || annotation.value().toUpperCase().startsWith("DELETE");
            Assert.isTrue(flag, String.format("This SQL [%s] may be not a UPDATE or DELETE SQL !", annotation.value()));

            sqlParam.setSql(annotation.value());
            sqlParam.setArgs(queryParam.getArgs());
            sqlParam.setParamMap(queryParam.getParamMap());
        } else {
            Assert.isTrue(queryParam.onlyOneArg(), "Use 'JPA Entity', just support one argument !");

            String sql = UPDATE + ReflectUtils.getTableName(queryParam.firstArg()) + " SET ";

            Map<String, Object> idMap = ReflectUtils.getIdValue(queryParam.firstArg());
            Map<String, Object> columnMap = ReflectUtils.getColumnValue(queryParam.firstArg());

            List<String> propertyStrs = new ArrayList<>();
            columnMap.keySet().forEach(k -> propertyStrs.add("`" + k + "` = ?"));

            List<String> idStrs = new ArrayList<>();
            idMap.keySet().forEach(k -> idStrs.add("`" + k + "` = ?"));

            sql += StringUtils.join(", ", propertyStrs);
            sql += " WHERE ";
            sql += StringUtils.join(", ", idStrs);

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

            sqlParam.setSql(sql);
            sqlParam.setArgs(newArgs);
        }

        log.debug("SQL statement [{}]", sqlParam.getSql());
        if (queryParam.isNamed()) {
            log.debug("SQL arguments [{}]", sqlParam.getParamMap());
        } else {
            log.debug("SQL arguments {}", Arrays.toString(sqlParam.getArgs()));
        }

        return sqlParam;
    }

}
