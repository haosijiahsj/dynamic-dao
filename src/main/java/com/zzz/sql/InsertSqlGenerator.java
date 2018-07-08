package com.zzz.sql;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zzz.annotations.Save;
import com.zzz.reflect.ReflectUtils;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        if (!"".equals(annotation.value())) {
            return SqlParam.of(annotation.value(), queryParam.getArgs(), queryParam.getParamMap());
        }

        Preconditions.checkArgument(queryParam.onlyOneArg(), "在保存时，使用对象保存模式下，仅能传入一个参数！");

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

        log.debug("生成的sql语句为：[{}]", sql);

        Object[] newArgs = new Object[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            newArgs[i] = map.get(key);
            i++;
        }

        return SqlParam.of(sql, newArgs);
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
                Preconditions.checkArgument(idValue != null, "id生成策略非递增，需要传入id值！");
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
