package com.zzz.processor.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zzz.annotations.Save;
import com.zzz.processor.BaseMethodProcessor;
import com.zzz.reflect.ReflectUtils;
import com.zzz.support.QueryParam;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
@SuppressWarnings("unchecked")
public class SaveMethodProcessor extends BaseMethodProcessor<Save> {

    private static final String INSERT = "INSERT INTO ";

    @Override
    public Object process() throws Exception {
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        // 在Save注解上传入了sql
        if (!"".equals(annotation.value())) {
            if (queryParam.isNamed()) {
                if (annotation.returnKey()) {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(annotation.value(), new MapSqlParameterSource(queryParam.getParamMap()), keyHolder);
                    return Objects.requireNonNull(keyHolder.getKey()).intValue();
                }

                return namedParameterJdbcTemplate.update(annotation.value(), queryParam.getParamMap());
            } else {
                if (annotation.returnKey()) {
                    return this.updateForReturnKey(annotation.value(), queryParam.getArgs());
                }
                return jdbcTemplate.update(annotation.value(), queryParam.getArgs());
            }
        }

        Preconditions.checkArgument(queryParam.onlyOneArg(), "在保存时，使用对象保存模式下，仅能传入一个参数！");
        Map<String, Object> map = ReflectUtils.getColumnValue(queryParam.firstArg());
        this.handleIdColumn(queryParam.firstArg(), map);
        String sql = this.buildSql(queryParam.firstArg(), map);
        Object[] objects = new Object[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            objects[i] = map.get(key);
            i++;
        }

        if (annotation.returnKey()) {
            return this.updateForReturnKey(sql, objects);
        }

        return jdbcTemplate.update(sql, objects);
    }

    /**
     * 处理主键列
     * @param arg
     * @param map
     */
    private void handleIdColumn(Object arg, Map<String, Object> map) throws Exception {
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
            Column columnAnno = field.getAnnotation(Column.class);
            if (columnAnno == null) {
                continue;
            }

            // 若主键不是自增的，则需要获取用户传入的值
            if (!generatedValueAnno.strategy().equals(GenerationType.IDENTITY)) {
                String columnName = "".equals(columnAnno.name()) ? field.getName() : columnAnno.name();
                map.put(columnName, field.get(arg));
            }
        }
    }

    /**
     * 组装sql语句
     * @param arg
     * @param map
     * @return
     */
    private String buildSql(Object arg, Map<String, Object> map) {
        String sql = INSERT + ReflectUtils.getTableName(arg) + " ";

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

        return sql;
    }

    /**
     * 获取生成的主键
     * @param sql
     * @param objects
     * @return
     */
    private int updateForReturnKey(String sql, Object[] objects) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < objects.length; i++) {
                int index = i + 1;
                ps.setObject(index, objects[i]);
            }

            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

}
