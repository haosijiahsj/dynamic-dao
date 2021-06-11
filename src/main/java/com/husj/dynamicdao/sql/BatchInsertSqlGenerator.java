package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.BatchSave;
import com.husj.dynamicdao.annotations.mapping.GenerationType;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.reflect.MappingUtils;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.reflect.definition.ColumnDefinition;
import com.husj.dynamicdao.reflect.definition.TableDefinition;
import com.husj.dynamicdao.support.IdentifierGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class BatchInsertSqlGenerator extends BaseSqlGenerator<BatchSave> {

    private static final String INSERT = "INSERT INTO `%s` (%s) VALUES (%s)";

    public BatchInsertSqlGenerator(Method method, BatchSave annotation, QueryParam queryParam, JdbcTemplate jdbcTemplate) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
        super.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SqlParam generateSql() throws Exception {
        SqlParam sqlParam = new SqlParam();
        ResolvableType resolvableType = ResolvableType.forMethodParameter(method, 0);
        Class<?> genericClass = resolvableType.getGeneric(0).resolve();

        TableDefinition tableDefinition = MappingUtils.getTableDefinitionByClass(genericClass);
        List<ColumnDefinition> columnDefinitions = tableDefinition.getColumnDefinitions();
        ColumnDefinition idColumnDefinition = tableDefinition.getIdColumnDefinition();

        List<String> propertyStrs = new ArrayList<>();
        List<String> markStrs = new ArrayList<>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.isPrimaryKey() && GenerationType.IDENTITY.equals(columnDefinition.getGenerationType())) {
                continue;
            }
            propertyStrs.add("`" + columnDefinition.getColumnName() + "`");
            markStrs.add(":" + columnDefinition.getColumnName());
        }
        String sql = String.format(INSERT, tableDefinition.getTableName(), String.join(", ", propertyStrs),
                String.join(", ", markStrs));

        List<Map<String, Object>> argMaps = new ArrayList<>();
        Collection<?> coll = (Collection<?>) queryParam.firstArg();
        for (Object arg : coll) {
            Map<String, Object> map = ReflectUtils.getColumnValue(arg, tableDefinition);
            this.processIdColumn(arg, idColumnDefinition, map);

            argMaps.add(map);
        }
        sqlParam.setSql(sql);
        sqlParam.setArgMaps(argMaps);


        log.debug("SQL statement [{}]", sqlParam.getSql());
        log.debug("SQL arguments {}", sqlParam.getArgMaps());

        return sqlParam;
    }

    private void processIdColumn(Object arg, ColumnDefinition idColumnDefinition, Map<String, Object> map) throws Exception {
        GenerationType generationType = idColumnDefinition.getGenerationType();
        // 这种不需要处理id
        if (GenerationType.IDENTITY.equals(generationType)) {
            return;
        }

        if (GenerationType.ASSIGNED.equals(generationType)) {
            // 自行传入id值
            map.put(idColumnDefinition.getColumnName(), ReflectUtils.getObjectValue(arg, idColumnDefinition.getField()));
        } else if (GenerationType.UUID.equals(generationType)) {
            // 生成一个uuid
            String idValue = UUID.randomUUID().toString().replaceAll("-", "");
            map.put(idColumnDefinition.getColumnName(), idValue);
        } else if (GenerationType.GENERATED.equals(generationType)) {
            Class<?> generator = idColumnDefinition.getGenerator();
            if (void.class.equals(generator)) {
                throw new DynamicDaoException("自定义主键需要指定生成类!");
            }
            IdentifierGenerator identifierGenerator = (IdentifierGenerator) generator.newInstance();
            map.put(idColumnDefinition.getColumnName(), identifierGenerator.nextKey(jdbcTemplate, arg));
        } else {
            throw new DynamicDaoException("not support !");
        }
    }

}
