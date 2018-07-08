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

        // 在Save注解上传入了sql
        if (!"".equals(annotation.value())) {
            if (queryParam.isNamed()) {
                if (annotation.returnKey()) {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(sqlParam.getSql(), new MapSqlParameterSource(sqlParam.getParamMap()), keyHolder);
                    return Objects.requireNonNull(keyHolder.getKey()).intValue();
                }

                return namedParameterJdbcTemplate.update(sqlParam.getSql(), sqlParam.getParamMap());
            } else {
                if (annotation.returnKey()) {
                    return this.updateForReturnKey(sqlParam.getSql(), sqlParam.getArgs());
                }
                return jdbcTemplate.update(sqlParam.getSql(), sqlParam.getArgs());
            }
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
    private long updateForReturnKey(String sql, Object[] objects) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < objects.length; i++) {
                int index = i + 1;
                ps.setObject(index, objects[i]);
            }

            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

}
