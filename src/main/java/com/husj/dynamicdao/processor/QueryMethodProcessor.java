package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.Query;
import com.husj.dynamicdao.annotations.query.MapperIgnore;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.page.PageParam;
import com.husj.dynamicdao.page.PageWrapper;
import com.husj.dynamicdao.reflect.ReflectUtils;
import com.husj.dynamicdao.sql.BaseSqlGenerator;
import com.husj.dynamicdao.sql.SelectSqlGenerator;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.CollectionUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@SuppressWarnings("unchecked")
public class QueryMethodProcessor<T> extends BaseMethodProcessor<Query> {

    @Override
    public Object process() throws Exception {
        this.preCheck();

        // 查询参数
        QueryParam queryParam = new QueryParam(args, argsAnnotations);
        BaseSqlGenerator<Query> sqlGenerator = new SelectSqlGenerator(method, annotation, queryParam);
        SqlParam sqlParam = sqlGenerator.generateSql();

        // 查询数据库
        List<Map<String, Object>> mapList = this.query(sqlParam, sqlGenerator, queryParam);

        // 返回值是PageWrapper
        if (queryParam.isPageQuery() && PageWrapper.class.equals(method.getReturnType())) {
            SqlParam countSqlParam = sqlGenerator.generateCountSql(sqlParam.getSql());
            return this.processPageQueryResult(countSqlParam, queryParam, mapList);
        }

        // 普通返回值
        return this.processQueryResult(mapList);
    }

    /**
     * 预检查
     */
    private void preCheck() {
        // 查询方法不允许返回void
        if (void.class.equals(method.getReturnType()) || Void.class.equals(method.getReturnType())) {
            throw new IllegalArgumentException("Query can't return void type !");
        }
    }

    /**
     * 查询方法
     *
     * @param sqlParam
     * @param sqlGenerator
     * @param queryParam
     * @return
     */
    private List<Map<String, Object>> query(SqlParam sqlParam, BaseSqlGenerator sqlGenerator, QueryParam queryParam) {
        // 具名参数
        if (queryParam.isNamed()) {
            if (queryParam.isPageQuery()) {
                SqlParam pageSqlParam = sqlGenerator.generatePageSql(sqlParam.getSql());
                return namedParameterJdbcTemplate.queryForList(pageSqlParam.getSql(), pageSqlParam.getParamMap());
            }

            return namedParameterJdbcTemplate.queryForList(sqlParam.getSql(), sqlParam.getParamMap());
        }
        // ?占位符
        if (queryParam.isPageQuery()) {
            SqlParam pageSqlParam = sqlGenerator.generatePageSql(sqlParam.getSql());
            return jdbcTemplate.queryForList(pageSqlParam.getSql(), pageSqlParam.getArgs());
        }

        return jdbcTemplate.queryForList(sqlParam.getSql(), sqlParam.getArgs());
    }

    /**
     * 有MapperIgnore注解
     *
     * @return
     */
    private String getMappperIgnoreString() {
        String ignoreString = "";
        for (Annotation methodAnnotation : methodAnnotations) {
            if (MapperIgnore.class == methodAnnotation.annotationType()) {
                ignoreString = ((MapperIgnore) methodAnnotation).value();
                break;
            }
        }

        return ignoreString;
    }

    /**
     * 处理普通查询情况
     *
     * @param mapList
     * @return
     * @throws Exception
     */
    private Object processQueryResult(List<Map<String, Object>> mapList) throws Exception {
        Class genericClass = Void.class;
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        Class resolve = resolvableType.getGeneric(0).resolve();

        // 若没有获取到泛型真实类型，说明返回值并不是泛型，Map为特例，泛型真实类型为Map，无需处理直接返回
        if (resolve != null && !Map.class.equals(resolve) && !Map.class.equals(method.getReturnType())) {
            genericClass = resolve;
        }

        // 返回值不是泛型则默认用户返回的是实体
        if (resolve == null || Map.class.equals(method.getReturnType())) {
            genericClass = method.getReturnType();
        }

        // 若返回的实体不是Void
        if (!Void.class.equals(genericClass)) {
            // 是否是支持的单列数据，支持的则返回单列数据如List<Long>, Long等
            if (this.isSupportSingleColumnType(genericClass)) {
                return this.processSingleColumnResult(mapList, genericClass);
            }

            String ignoreString = this.getMappperIgnoreString();
            // 返回的不是泛型List或者Set, 返回的是单个对象或者Map<String, Object>
            if (genericClass.equals(method.getReturnType())) {
                if (mapList.isEmpty()) {
                    return Map.class.equals(genericClass) ? Collections.emptyMap() : null;
                }

                Assert.isTrue(mapList.size() <= 1, String.format("Except 1 row return value, actual %s !", mapList.size()));
                // 返回第一行记录
                return Map.class.equals(genericClass) ? mapList.get(0) : ReflectUtils.rowMapping(mapList, genericClass, ignoreString).get(0);
            }

            // mapList值封装到实体中
            List<Object> results = ReflectUtils.rowMapping(mapList, genericClass, ignoreString);
            if (Set.class.equals(method.getReturnType())) {
                return new HashSet<>(results);
            }

            return results;
        }

        // 返回List<Map<String, Object>>
        return mapList;
    }

    /**
     * 判断返回的数据类型是否支持
     *
     * @param requiredType
     * @return
     */
    private boolean isSupportSingleColumnType(Class requiredType) {
        // 泛型真实类型是String
        if (String.class.equals(requiredType)) {
            return true;
        }

        // 泛型真实类型是数字类型
        if (Number.class.isAssignableFrom(requiredType)) {
            return true;
        }

        return false;
    }

    /**
     * 处理单列结果
     *
     * @param mapList
     * @return
     */
    private Object processSingleColumnResult(List<Map<String, Object>> mapList, Class requiredType) {
        List<Object> singleColumnList =  mapList.stream().map(m -> {
            Assert.isTrue(m.size() <= 1, String.format("Single column except 1 column return value, actual %s", m.size()));
            Optional<Object> o = m.values().stream().findFirst();
            if (!o.isPresent()) {
                return null;
            }
            Object value = m.values().stream().findFirst().get();

            // 支持返回String和数字类如Integer, Long
            if (String.class.equals(requiredType)) {
                return value.toString();
            } else if (Number.class.isAssignableFrom(requiredType)) {
                return value instanceof Number ? NumberUtils.convertNumberToTargetClass((Number) value, requiredType) :
                        NumberUtils.parseNumber(value.toString(), requiredType);
            }

            throw new IllegalArgumentException("Value [" + value + "] is of type [" + value.getClass().getName() + "] and cannot be converted to required type [" + requiredType.getName() + "]");
        }).collect(Collectors.toList());

        // 若返回值是set
        if (Set.class.equals(method.getReturnType())) {
            return new HashSet<>(singleColumnList);
        }

        // 返回第一个元素
        if (requiredType.equals(method.getReturnType())) {
            if (singleColumnList.isEmpty()) {
                return null;
            }

            Assert.isTrue(singleColumnList.size() <= 1, String.format("except 1 row, actual %s", singleColumnList.size()));
            return singleColumnList.get(0);
        }

        return singleColumnList;
    }

    /**
     * 处理分页情况
     *
     * @param countSqlParam
     * @param mapList
     * @param queryParam
     * @return
     * @throws Exception
     */
    private Object processPageQueryResult(SqlParam countSqlParam, QueryParam queryParam, List<Map<String, Object>> mapList) throws Exception {
        PageParam pageParam = queryParam.getPageParam();
        // 查询总条数
        long totalRows = this.countQuery(countSqlParam, queryParam);
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

        // 获取泛型真实类型
        Class genericClass = Void.class;
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        Class resolve = resolvableType.getGeneric(0).resolve();
        if (resolve != null && !Map.class.equals(resolve)) {
            genericClass = resolve;
        }

        if (Void.class.equals(genericClass)) {
            pageWrapper.setContent((List<T>) mapList);
        } else {
            if (this.isSupportSingleColumnType(genericClass)) {
                pageWrapper.setContent((List<T>) this.processSingleColumnResult(mapList, genericClass));
            } else {
                pageWrapper.setContent((List<T>) ReflectUtils.rowMapping(mapList, genericClass, this.getMappperIgnoreString()));
            }
        }

        return pageWrapper;
    }

    /**
     * 查询总条数
     *
     * @param sqlParam
     * @param queryParam
     * @return
     */
    private Long countQuery(SqlParam sqlParam, QueryParam queryParam) {
        Long totalRows;
        if (queryParam.isNamed()) {
            totalRows = namedParameterJdbcTemplate.queryForObject(sqlParam.getSql(), sqlParam.getParamMap(), Long.class);
        } else {
            totalRows = jdbcTemplate.queryForObject(sqlParam.getSql(), sqlParam.getArgs(), Long.class);
        }

        return totalRows == null ? 0L : totalRows;
    }

}
