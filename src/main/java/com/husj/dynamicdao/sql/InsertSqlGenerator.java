package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.SqlParseUtils;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
            Assert.isTrue(flag, String.format("This SQL [%s] may be not a INSERT SQL !", annotation.value()));

            if (queryParam.isNamed()) {
                sqlParam = SqlParseUtils.parseNamedSql(annotation.value(), queryParam.getParamMap());
            } else {
                sqlParam.setSql(annotation.value());
                sqlParam.setArgs(queryParam.getArgs());
            }
        } else {
            Assert.isTrue(queryParam.onlyOneArg(), "Use 'JPA Entity', just support one argument !");

            Map<String, Object> map = ReflectUtils.getColumnValue(queryParam.firstArg());
            this.processIdColumn(queryParam.firstArg(), map);

            String sql = INSERT + ReflectUtils.getTableName(queryParam.firstArg()) + " ";

            List<String> propertyStrs = new ArrayList<>();
            List<String> markStrs = new ArrayList<>();
            map.keySet().forEach(k -> {
                propertyStrs.add("`" + k + "`");
                markStrs.add("?");
            });

            sql += "(" + StringUtils.join(", ", propertyStrs) + ")";
            sql += " VALUES ";
            sql += "(" + StringUtils.join(", ", markStrs) + ")";

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
        log.debug("SQL arguments {}", Arrays.toString(sqlParam.getArgs()));

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
                Assert.isTrue(idValue != null, "Need id value !");
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
