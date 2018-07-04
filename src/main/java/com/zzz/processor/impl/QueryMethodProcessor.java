package com.zzz.processor.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzz.annotations.Query;
import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;
import com.zzz.processor.BaseMethodProcessor;
import com.zzz.support.QueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@SuppressWarnings("unchecked")
public class QueryMethodProcessor<T> extends BaseMethodProcessor<Query> {

    private static final String WHERE = " WHERE";
    private static final String COLON = ":";
    private static final String LIMIT = " LIMIT ";

    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+DISTINCT\\s+(\\S+)\\s+FROM\\s+.+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_PATTERN = Pattern.compile("^.*\\s*(FROM)\\s+.+", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("\\s+(ORDER\\s+BY)\\s+[^'\"]", Pattern.CASE_INSENSITIVE);
    private static final Pattern GROUP_BY_PATTERN = Pattern.compile("\\s+(GROUP\\s+BY)\\s+[^'\"]", Pattern.CASE_INSENSITIVE);

    @Override
    public Object process() throws Exception {
        this.checkReturnType();
        // 查询参数
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        String sql = this.buildSql(queryParam);

        // 没有参数直接查询
        if (args.length == 0) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return this.processQueryResult(list);
        }

        // 处理查询当仅传入PageParam参数时
        if (args.length == 1 && queryParam.getPageParam() != null) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(this.buildPageSql(sql, queryParam));
            // 返回值是PageWrapper
            if (queryParam.getPageParam() != null && PageWrapper.class.equals(method.getReturnType())) {
                return this.processPageQueryResult(sql, list, queryParam);
            } else {
                return this.processQueryResult(list);
            }
        }

        // 使用的是?占位符方式，此方式不支持分页
        if (!annotation.named()) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args);
            return this.processQueryResult(list);
        }

        String executeSql = sql;
        if (queryParam.getPageParam() != null) {
            executeSql = this.buildPageSql(sql, queryParam);
        }
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(executeSql, queryParam.getParamMap());
        // 返回值是PageWrapper
        if (queryParam.getPageParam() != null && PageWrapper.class.equals(method.getReturnType())) {
            return this.processPageQueryResult(sql, list, queryParam);
        } else {
            return this.processQueryResult(list);
        }
    }

    /**
     * 校验查询返回值
     */
    private void checkReturnType() {
        // 返回类型可以与目标类型相同
        if (annotation.entityClass().equals(method.getReturnType())) {
            return;
        }
        boolean check = List.class.equals(method.getReturnType()) || PageWrapper.class.equals(method.getReturnType());
        Preconditions.checkArgument(check, String.format("查询返回值必须为List或者是PageWrapper或与targetClass相同，该查询返回值为：[%s]！", method.getReturnType().getName()));
    }

    /**
     * 处理条件参数
     *
     * @param method
     * @param paramMap
     * @return
     */
    private String processConditions(Method method, Map<String, Object> paramMap) {
        Conditions conditionsAnno = method.getAnnotation(Conditions.class);
        if (conditionsAnno == null) {
            return "";
        }

        StringBuilder conditionSql = new StringBuilder();
        Condition[] conditionAnnos = conditionsAnno.value();
        for (int i = 0; i < conditionAnnos.length; i++) {
            String conditionStr = conditionAnnos[i].value();
            int index = conditionStr.indexOf(COLON);
            // 若condition中表达式没有占位符
            if (index == -1) {
                if (i == 0) {
                    conditionSql.append(" ").append(conditionStr);
                } else {
                    conditionSql.append(" ").append(conditionAnnos[i].operator().getOperator()).append(" ").append(conditionStr);
                }
                continue;
            }
            String key = conditionStr.substring(index + 1);
            Object value = paramMap.get(key);

            if (value != null) {
                if (i == 0) {
                    conditionSql.append(" ").append(conditionStr);
                } else {
                    conditionSql.append(" ").append(conditionAnnos[i].operator().getOperator()).append(" ").append(conditionStr);
                }
            }
        }
        // 所有条件没有拼接上不添加WHERE字符
        if (StringUtils.isEmpty(conditionSql.toString())) {
            return "";
        }

        return WHERE + conditionSql.toString();
    }

    /**
     * 组装sql语句
     *
     * @param queryParam
     * @return
     */
    private String buildSql(QueryParam queryParam) {
        String sql = annotation.value() + this.processConditions(method, queryParam.getParamMap());
        log.debug("生成的sql: [{}]", sql);

        return sql;
    }

    /**
     * 组装分页sql
     *
     * @param queryParam
     * @return
     */
    private String buildPageSql(String sql, QueryParam queryParam) {
        int offset = queryParam.getPageParam().getSize() * (queryParam.getPageParam().getPage() - 1);

        String pageSql = sql + LIMIT + offset + ", " + queryParam.getPageParam().getSize();
        log.debug("生成的分页sql: [{}]", pageSql);

        return pageSql;
    }

    /**
     * 组装查询总条数sql
     *
     * @param sql
     * @return
     */
    private String buildCountSql(String sql) {
        // 拼接头部
        Matcher selectMatcher = SELECT_PATTERN.matcher(sql);
        String countSql = selectMatcher.find() ? "SELECT COUNT(" + selectMatcher.group(1) + ") " : "SELECT COUNT(1) ";

        // 去掉FROM前面的字符
        Matcher fromMatcher = FROM_PATTERN.matcher(sql);
        int indexOfFrom = fromMatcher.find() ? fromMatcher.start(1) : -1;
        sql = sql.substring(indexOfFrom);

        // 去掉ORDER BY后面的字符
        Matcher orderByMatcher = ORDER_BY_PATTERN.matcher(sql);
        int indexOfOrderBy = orderByMatcher.find() ? orderByMatcher.start(1) : -1;
        sql = indexOfOrderBy != -1 ? sql.substring(0, indexOfOrderBy) : sql;

        // 若sql中含有GROUP BY，则还需要在外层包装一个COUNT(1)
        Matcher groupByMatcher = GROUP_BY_PATTERN.matcher(sql);
        sql = countSql + sql;
        if (groupByMatcher.find()) {
            sql = "SELECT COUNT(1) FROM (" + sql + ") AS tmp";
        }

        log.debug("生成的count sql: [{}]", sql);

        return sql;
    }

    /**
     * 映射
     *
     * @param mapList
     * @param targetClass
     * @return
     * @throws Exception
     */
    private List<Object> rowMap(List<Map<String, Object>> mapList, Class targetClass) throws Exception {
        // 将查询结果key转换为大写
        List<Map<String, Object>> resultList = mapList.stream().map(map -> {
            Map<String, Object> resultMap = Maps.newHashMap();
            map.keySet().forEach(k -> resultMap.put(k.toUpperCase(), map.get(k)));
            return resultMap;
        }).collect(Collectors.toList());

        Field[] fields = targetClass.getDeclaredFields();
        List<Object> objects = Lists.newArrayList();

        for (Map<String, Object> map : resultList) {
            Object object = targetClass.newInstance();
            for (Field field : fields) {
                Object value = map.get(field.getName().toUpperCase());
                if (value == null) {
                    continue;
                }

                Class<?> fieldType = field.getType();
                Class<?> columnType = value.getClass();

                field.setAccessible(true);
                // 对象类型与列类型相同
                if (fieldType.equals(columnType)) {
                    field.set(object, value);
                } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
                    field.set(object, Byte.valueOf(value.toString()));
                } else if (Boolean.class.equals(columnType) || boolean.class.equals(columnType)) {
                    if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                        field.set(object, value);
                    } else {
                        if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
                            field.set(object, (Boolean) value ? (short) 1 : (short) 0);
                        } else {
                            field.set(object, (Boolean) value ? (byte) 1 : (byte) 0);
                        }
                    }
                } else if (BigDecimal.class.equals(fieldType)) {
                    field.set(object, new BigDecimal(value.toString()));
                } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                    field.set(object, Integer.valueOf(value.toString()));
                } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
                    field.set(object, Long.valueOf(value.toString()));
                } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
                    field.set(object, Short.valueOf(value.toString()));
                } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
                    field.set(object, Float.valueOf(value.toString()));
                } else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
                    field.set(object, Double.valueOf(value.toString()));
                } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
                    if (Boolean.class.equals(value) || boolean.class.equals(value)) {
                        field.set(object, value);
                    } else {
                        field.set(object, "1".equals(value.toString()));
                    }
                } else if (Timestamp.class.equals(columnType)) {
                    // 支持java8日期
                    if (LocalDateTime.class.equals(fieldType)) {
                        Instant instant = ((Date) value).toInstant();
                        ZoneId zoneId = ZoneId.systemDefault();
                        field.set(object, instant.atZone(zoneId).toLocalDateTime());
                    } else {
                        field.set(object, new Date(((Timestamp) value).getTime()));
                    }
                } else if (java.sql.Date.class.equals(columnType)) {
                    // 支持java8日期
                    if (LocalDate.class.equals(fieldType)) {
                        Instant instant = ((Date) value).toInstant();
                        ZoneId zoneId = ZoneId.systemDefault();
                        field.set(object, instant.atZone(zoneId).toLocalDate());
                    } else {
                        field.set(object, new Date(((java.sql.Date) value).getTime()));
                    }
                } else if (fieldType.isEnum()) {
                    Enum<?> enums = Enum.valueOf((Class<Enum>) fieldType, value.toString());
                    field.set(object, enums);
                } else {
                    log.warn("类型不匹配 column类型 :[{}] filed类型: [{}] , value: [{}]", columnType, fieldType, value);
                }

                field.setAccessible(false);
            }

            objects.add(object);
        }

        return objects;
    }

    /**
     * 处理普通查询情况
     *
     * @param mapList
     * @return
     * @throws Exception
     */
    private Object processQueryResult(List<Map<String, Object>> mapList) throws Exception {
        if (CollectionUtils.isEmpty(mapList)) {
            if (!Void.class.equals(annotation.entityClass()) && annotation.entityClass().equals(method.getReturnType())) {
                return null;
            }

            return Collections.emptyList();
        }
        if (Void.class.equals(annotation.entityClass())) {
            return mapList;
        } else {
            if (annotation.entityClass().equals(method.getReturnType())) {
                Preconditions.checkArgument(mapList.size() == 1, String.format("查询列表有%s条数据大于1条数据！", mapList.size()));
                return this.rowMap(mapList, annotation.entityClass()).get(0);
            }

            return this.rowMap(mapList, annotation.entityClass());
        }
    }

    /**
     * 处理分页情况
     *
     * @param sql
     * @param mapList
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Object processPageQueryResult(String sql, List<Map<String, Object>> mapList, QueryParam queryParam) throws Exception {
        PageParam pageParam = queryParam.getPageParam();
        // 查询总条数
        long totalRows = this.countQuery(sql, queryParam.getParamMap());
        int totalPages = 0;
        if (totalRows != 0) {
            totalPages = ((int) (totalRows / pageParam.getSize())) + (totalRows % pageParam.getSize() == 0 ? 0 : 1);
        }

        PageWrapper<T> pageWrapper = new PageWrapper<>();
        pageWrapper.setCurPage(pageParam.getPage());
        pageWrapper.setSize(pageParam.getSize());
        pageWrapper.setTotalRows(totalRows);
        pageWrapper.setTotalPages(totalPages);

        if (CollectionUtils.isEmpty(mapList)) {
            pageWrapper.setContent(Collections.emptyList());
            return pageWrapper;
        }

        if (Void.class.equals(annotation.entityClass())) {
            pageWrapper.setContent((List<T>) mapList);
        } else {
            List<Object> queryList = this.rowMap(mapList, annotation.entityClass());
            pageWrapper.setContent((List<T>) queryList);
        }
        return pageWrapper;
    }

    /**
     * 查询总条数
     *
     * @param sql
     * @param paramMap
     * @return
     */
    private long countQuery(String sql, Map<String, Object> paramMap) {
        Long totalRows = namedParameterJdbcTemplate.queryForObject(this.buildCountSql(sql), paramMap, Long.class);

        return totalRows == null ? 0L : totalRows;
    }
}
