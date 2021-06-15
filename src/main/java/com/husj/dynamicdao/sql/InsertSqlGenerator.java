package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.annotations.mapping.GenerationType;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.reflect.MappingUtils;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.reflect.definition.ColumnDefinition;
import com.husj.dynamicdao.reflect.definition.TableDefinition;
import com.husj.dynamicdao.support.IdentifierGenerator;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.SqlParseUtils;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class InsertSqlGenerator extends BaseSqlGenerator<Save> {

    private static final String INSERT = "INSERT INTO `%s` (%s) VALUES (%s)";

    public InsertSqlGenerator(Method method, Save annotation, QueryParam queryParam, JdbcTemplate jdbcTemplate) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
        super.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SqlParam generateSql() throws Exception {
        SqlParam sqlParam = new SqlParam();
        if (StringUtils.isNotEmpty(annotation.value())) {
            boolean flag = annotation.value().toUpperCase().startsWith("INSERT");
            Assert.isTrue(flag, String.format("This SQL [%s] may be not a INSERT SQL !", annotation.value()));

            if (queryParam.isNamed()) {
                sqlParam = SqlParseUtils.parseNamedSql(annotation.value(), queryParam.getParamMap());
            } else {
                sqlParam.setSql(annotation.value());
                sqlParam.setArgs(queryParam.getArgs());
            }
        } else {
            Assert.isTrue(queryParam.onlyOneArg(), "Use 'Entity', just support one argument !");

            TableDefinition tableDefinition = MappingUtils.getTableDefinitionByClass(queryParam.firstArg().getClass());
            Map<String, Object> map = ReflectUtils.getColumnValue(queryParam.firstArg(), tableDefinition);

            ColumnDefinition idColumnDefinition = tableDefinition.getIdColumnDefinition();
            this.processIdColumn(queryParam.firstArg(), idColumnDefinition, map);

            List<String> propertyStrs = new ArrayList<>();
            List<String> markStrs = new ArrayList<>();
            map.keySet().forEach(k -> {
                propertyStrs.add("`" + k + "`");
                markStrs.add("?");
            });

            String sql = String.format(INSERT, tableDefinition.getTableName(), String.join(", ", propertyStrs),
                    String.join(", ", markStrs));

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
            // 使用一个uuid处理值
            String idValue = UUID.randomUUID().toString().replaceAll("-", "");
            map.put(idColumnDefinition.getColumnName(), idValue);
        } else if (GenerationType.GENERATED.equals(generationType)) {
            Class<? extends IdentifierGenerator> generator = idColumnDefinition.getGenerator();
            if (IdentifierGenerator.class.equals(generator)) {
                throw new DynamicDaoException("自定义主键需要指定生成类!");
            }
            IdentifierGenerator identifierGenerator = generator.newInstance();
            map.put(idColumnDefinition.getColumnName(), identifierGenerator.nextKey(jdbcTemplate, arg));
        } else {
            throw new DynamicDaoException("not support !");
        }
    }

}
