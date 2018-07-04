package com.zzz.processor.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.zzz.annotations.Update;
import com.zzz.processor.BaseMethodProcessor;
import com.zzz.reflect.ReflectUtils;
import com.zzz.support.QueryParam;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@SuppressWarnings("unchecked")
public class UpdateMethodProcessor extends BaseMethodProcessor<Update> {

    private static final String UPDATE = "UPDATE ";

    @Override
    public Object process() {
        Object arg = args[0];
        // 在Update注解上传入了sql
        if (!"".equals(annotation.value())) {
            if (annotation.named()) {
                Preconditions.checkArgument(args.length > 0, "在更新时，使用占位保存模式下，仅能传入一个参数！");
                QueryParam queryParam = new QueryParam(args, argsAnnotations);

                return namedParameterJdbcTemplate.update(annotation.value(), queryParam.getParamMap());
            } else {
                return jdbcTemplate.update(annotation.value(), args);
            }
        }

        Map<String, Object> idMap = ReflectUtils.getIdValue(arg);
        Map<String, Object> columnMap = ReflectUtils.getColumnValue(arg);

        String sql = this.buildSql(arg, idMap, columnMap);
        Object[] params = new Object[idMap.size() + columnMap.size()];

        int i = 0;
        for (String key : columnMap.keySet()) {
            params[i] = columnMap.get(key);
            i++;
        }
        for (String key : idMap.keySet()) {
            params[i] = idMap.get(key);
            i++;
        }

        return jdbcTemplate.update(sql, params);
    }

    private String buildSql(Object arg, Map<String, Object> idMap, Map<String, Object> columnMap) {
        String sql = UPDATE + ReflectUtils.getTableName(arg) + " SET ";

        List<String> propertyStrs = Lists.newArrayList();
        columnMap.keySet().forEach(k -> propertyStrs.add("`" + k + "` = ?"));

        List<String> idStrs = Lists.newArrayList();
        idMap.keySet().forEach(k -> idStrs.add("`" + k + "` = ?"));

        sql += Joiner.on(", ").join(propertyStrs);
        sql += " WHERE ";
        sql += Joiner.on(", ").join(idStrs);

        log.debug("生成的sql语句为：[{}]", sql);

        return sql;
    }

}
