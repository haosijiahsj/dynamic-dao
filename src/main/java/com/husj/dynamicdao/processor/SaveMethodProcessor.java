package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.sql.BaseSqlGenerator;
import com.husj.dynamicdao.sql.InsertSqlGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

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

        return this.convertNumber2OtherType(keyHolder.getKey());
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

        return this.convertNumber2OtherType(keyHolder.getKey());
    }

    /**
     * 将返回的主键转换到需要的类型
     * 仅支持 短整型，整型，长整型，字符串类型
     * @param key
     * @return
     */
    private Object convertNumber2OtherType(Number key) {
        if (Short.class.equals(method.getReturnType()) || short.class.equals(method.getReturnType())) {
            return key.shortValue();
        } else if (Integer.class.equals(method.getReturnType()) || int.class.equals(method.getReturnType())) {
            return key.intValue();
        } else if (Long.class.equals(method.getReturnType()) || long.class.equals(method.getReturnType())) {
            return key.longValue();
        } else if (String.class.equals(method.getReturnType())) {
            return key.toString();
        }

        return key;
    }

}
