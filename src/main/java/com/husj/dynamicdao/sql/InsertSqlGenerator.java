package com.husj.dynamicdao.sql;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class InsertSqlGenerator extends BaseSqlGenerator<Save> {

    private static final String INSERT = "INSERT INTO ";

    public InsertSqlGenerator(Method method, Save annotation, QueryParam queryParam) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
    }

    @Override
    public SqlParam generateSql() throws Exception {
        SqlParam sqlParam = new SqlParam();
        if (!"".equals(annotation.value())) {
            boolean flag = annotation.value().toUpperCase().startsWith("INSERT");
            Preconditions.checkArgument(flag, "This SQL [%s] may be not a INSERT SQL !", annotation.value());

            sqlParam.setSql(annotation.value());
            sqlParam.setArgs(queryParam.getArgs());
            sqlParam.setParamMap(queryParam.getParamMap());
        } else {
            Preconditions.checkArgument(queryParam.onlyOneArg(), "Use 'JPA Entity', just support one argument !");

            Map<String, Object> map = ReflectUtils.getColumnValue(queryParam.firstArg());
            this.processIdColumn(queryParam.firstArg(), map);

            String sql = INSERT + ReflectUtils.getTableName(queryParam.firstArg()) + " ";

            List<String> propertyStrs = Lists.newArrayList();
            List<String> markStrs = Lists.newArrayList();
            map.keySet().forEach(k -> {
                propertyStrs.add("`" + k + "`");
                markStrs.add("?");
            });

            sql += "(" + Joiner.on(", ").join(propertyStrs) + ")";
            sql += " VALUES ";
            sql += "(" + Joiner.on(", ").join(markStrs) + ")";

            Object[] newArgs = new Object[map.size()];
            int i = 0;
            for (String key : map.keySet()) {
                newArgs[i] = map.get(key);
                i++;
            }

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

    /**
     * 处理主键列
     * @param arg
     * @param map
     */
    private void processIdColumn(Object arg, Map<String, Object> map) throws Exception {
        Field[] fields = arg.getClass().getDeclaredFields();
        for (Field field : fields) {
            Id idAnno = field.getAnnotation(Id.class);
            if (idAnno == null) {
                continue;
            }
            GeneratedValue generatedValueAnno = field.getAnnotation(GeneratedValue.class);
            if (generatedValueAnno == null) {
                continue;
            }

            // 若主键不是自增的，则需要获取用户传入的值
            if (!generatedValueAnno.strategy().equals(GenerationType.IDENTITY)) {
                Object idValue = field.get(arg);
                Preconditions.checkArgument(idValue != null, "Need id value !");
                String columnName = field.getName();
                Column columnAnno = field.getAnnotation(Column.class);
                if (columnAnno != null && !"".equals(columnAnno.name())) {
                    columnName = columnAnno.name();
                }
                map.put(columnName, idValue);
            }
        }
    }

}
