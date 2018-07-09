package com.zzz.processor;

import com.zzz.annotations.Save;
import com.zzz.sql.BaseSqlGenerator;
import com.zzz.sql.InsertSqlGenerator;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
public class SaveMethodProcessor extends BaseMethodProcessor<Save> {

    @Override
    public Object process() throws Exception {
        // 参数预处理
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        BaseSqlGenerator<Save> sqlGenerator = new InsertSqlGenerator(method, annotation, queryParam);
        SqlParam sqlParam = sqlGenerator.generateSql();

        // 使用的是具名参数
        if (queryParam.isNamed()) {
            if (annotation.returnKey()) {
                return this.namedUpdateForReturnKey(sqlParam.getSql(), sqlParam.getParamMap());
            }

            return namedParameterJdbcTemplate.update(sqlParam.getSql(), sqlParam.getParamMap());
        }

        if (annotation.returnKey()) {
            return this.updateForReturnKey(sqlParam.getSql(), sqlParam.getArgs());
        }

        return jdbcTemplate.update(sqlParam.getSql(), sqlParam.getArgs());
    }

    /**
     * 获取生成的主键
     * @param sql
     * @param objects
     * @return
     */
    private Object updateForReturnKey(String sql, Object[] objects) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < objects.length; i++) {
                int index = i + 1;
                ps.setObject(index, objects[i]);
            }

            return ps;
        }, keyHolder);

        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (Integer.class.equals(method.getReturnType()) || int.class.equals(method.getReturnType())) {
            return Integer.valueOf(String.valueOf(key));
        }

        return key;
    }

    /**
     * 获取生成的主键named方式
     * @param sql
     * @param paramMap
     * @return
     */
    private Object namedUpdateForReturnKey(String sql, Map<String, Object> paramMap) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (Integer.class.equals(method.getReturnType()) || int.class.equals(method.getReturnType())) {
            return Integer.valueOf(String.valueOf(key));
        }

        return key;
    }

}
