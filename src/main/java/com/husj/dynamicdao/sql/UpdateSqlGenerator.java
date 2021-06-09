package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Update;
import com.husj.dynamicdao.reflect.MappingUtils;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.reflect.definition.ColumnDefinition;
import com.husj.dynamicdao.reflect.definition.TableDefinition;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.SqlParseUtils;
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

            if (queryParam.isNamed()) {
                sqlParam = SqlParseUtils.parseNamedSql(annotation.value(), queryParam.getParamMap());
            } else {
                sqlParam.setSql(annotation.value());
                sqlParam.setArgs(queryParam.getArgs());
            }
        } else {
            Assert.isTrue(queryParam.onlyOneArg(), "Use 'Entity', just support one argument !");

            TableDefinition tableDefinition = MappingUtils.getTableDefinitionByClass(queryParam.firstArg().getClass());
            String sql = UPDATE + tableDefinition.getTableName() + " SET ";

            ColumnDefinition idColumnDefinition = tableDefinition.getIdColumnDefinition();
            Object idValue = ReflectUtils.getObjectValue(queryParam.firstArg(), idColumnDefinition.getField());
            Map<String, Object> columnMap = ReflectUtils.getColumnValue(queryParam.firstArg(), tableDefinition);

            List<String> propertyStrs = new ArrayList<>();
            columnMap.keySet().forEach(k -> propertyStrs.add("`" + k + "` = ?"));


            sql += StringUtils.join(", ", propertyStrs);
            sql += " WHERE ";
            sql += "`" + idColumnDefinition.getColumnName() + "` = ?";

            Object[] newArgs = new Object[columnMap.size() + 1];

            int i = 0;
            for (String key : columnMap.keySet()) {
                newArgs[i] = columnMap.get(key);
                i++;
            }
            newArgs[i] = idValue;

            columnMap.put(idColumnDefinition.getColumnName(), idValue);

            sqlParam.setSql(sql);
            sqlParam.setArgs(newArgs);
        }

        log.debug("SQL statement [{}]", sqlParam.getSql());
        log.debug("SQL arguments {}", Arrays.toString(sqlParam.getArgs()));

        return sqlParam;
    }

}
