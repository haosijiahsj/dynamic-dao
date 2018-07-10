package com.zzz.processor;

import com.google.common.base.Preconditions;
import com.zzz.annotations.Query;
import com.zzz.annotations.query.SingleColumn;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;
import com.zzz.reflect.ReflectUtils;
import com.zzz.sql.BaseSqlGenerator;
import com.zzz.sql.SelectSqlGenerator;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;
import com.zzz.utils.CollectionUtils;

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
        this.checkReturnType();
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

        return this.processQueryResult(mapList);
    }

    /**
     * 校验查询返回值
     */
    private void checkReturnType() {
        // 返回类型可以与目标类型相同
        if (annotation.entityClass().equals(method.getReturnType())) {
            return;
        }

        SingleColumn singleColumnAnno = this.getSingleColumnAnnotation();
        boolean check = List.class.equals(method.getReturnType())
                || PageWrapper.class.equals(method.getReturnType())
                || Set.class.equals(method.getReturnType())
                || (singleColumnAnno != null && singleColumnAnno.returnFirst());

        Preconditions.checkArgument(check, String.format("Query return type must be 'List' or 'PageWrapper' or 'Set', this is [%s], not support !", method.getReturnType().getName()));
    }

    /**
     * 查询方法
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
     * 处理普通查询情况
     *
     * @param mapList
     * @return
     * @throws Exception
     */
    private Object processQueryResult(List<Map<String, Object>> mapList) throws Exception {
        // 若指定返回的实体不是Void
        if (!Void.class.equals(annotation.entityClass())) {
            if (annotation.entityClass().equals(method.getReturnType())) {
                Preconditions.checkArgument(mapList.size() <= 1, String.format("Except 1 return value, actual %s !", mapList.size()));
                if (mapList.isEmpty()) {
                    return null;
                }

                return ReflectUtils.rowMapping(mapList, annotation.entityClass()).get(0);
            }

            List<Object> results = ReflectUtils.rowMapping(mapList, annotation.entityClass());
            if (Set.class.equals(method.getReturnType())) {
                return new HashSet<>(results);
            }

            return results;
        }

        // 若方法上存在@SingleColumn注解
        SingleColumn singleColumnAnno = this.getSingleColumnAnnotation();
        if (singleColumnAnno != null) {
            if (mapList.isEmpty()) {
                return null;
            }

            return this.processSingleColumnResult(mapList, singleColumnAnno.returnFirst());
        }

        return mapList;
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

        if (Void.class.equals(annotation.entityClass())) {
            pageWrapper.setContent((List<T>) mapList);
        } else {
            List<Object> queryList = ReflectUtils.rowMapping(mapList, annotation.entityClass());
            pageWrapper.setContent((List<T>) queryList);
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
    private long countQuery(SqlParam sqlParam, QueryParam queryParam) {
        Long totalRows;
        if (queryParam.isNamed()) {
            totalRows = namedParameterJdbcTemplate.queryForObject(sqlParam.getSql(), sqlParam.getParamMap(), Long.class);
        } else {
            totalRows = jdbcTemplate.queryForObject(sqlParam.getSql(), sqlParam.getArgs(), Long.class);
        }

        return totalRows == null ? 0L : totalRows;
    }

    /**
     * 是否存在@SingleColumn这个注解
     * @return
     */
    private SingleColumn getSingleColumnAnnotation() {
        for (Annotation annotation : methodAnnotations) {
            if (SingleColumn.class.equals(annotation.annotationType())) {
                return (SingleColumn) annotation;
            }
        }

        return null;
    }

    /**
     * 处理单列结果
     * @param mapList
     * @return
     */
    private Object processSingleColumnResult(List<Map<String, Object>> mapList, boolean returnFirst) {
        List<Object> singleColumnList =  mapList.stream().map(m -> {
            Preconditions.checkArgument(m.size() <= 1, String.format("Single column except 1 return value, actual %s", m.size()));
            Optional<Object> o = m.values().stream().findFirst();
            if (!o.isPresent()) {
                return null;
            }
            return m.values().stream().findFirst().get();
        }).collect(Collectors.toList());

        // 若返回值是set
        if (Set.class.equals(method.getReturnType())) {
            return new HashSet<>(singleColumnList);
        }

        // 返回第一个元素
        if (returnFirst) {
            return singleColumnList.get(0);
        }

        return singleColumnList;
    }

}
