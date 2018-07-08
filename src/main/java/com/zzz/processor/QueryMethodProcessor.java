package com.zzz.processor;

import com.google.common.base.Preconditions;
import com.zzz.annotations.Query;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;
import com.zzz.reflect.ReflectUtils;
import com.zzz.sql.BaseSqlGenerator;
import com.zzz.sql.SelectSqlGenerator;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;
import com.zzz.utils.CollectionUtils;

import java.util.*;

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

        // 使用的是?占位符方式
        if (!queryParam.isNamed()) {
            // 分页查询
            List<Map<String, Object>> list;
            if (queryParam.isPageQuery()) {
                SqlParam pageSqlParam = sqlGenerator.generatePageSql(sqlParam.getSql());
                list = jdbcTemplate.queryForList(pageSqlParam.getSql(), pageSqlParam.getArgs());
            } else {
                list = jdbcTemplate.queryForList(sqlParam.getSql(), sqlParam.getArgs());
            }

            // 返回值是PageWrapper
            if (queryParam.isPageQuery() && PageWrapper.class.equals(method.getReturnType())) {
                SqlParam countSqlParam = sqlGenerator.generateCountSql(sqlParam.getSql());
                return this.processPageQueryResult(countSqlParam, list, queryParam);
            } else {
                return this.processQueryResult(list);
            }
        }

        // 使用的是命名占位符方式
        List<Map<String, Object>> list;
        if (queryParam.isPageQuery()) {
            SqlParam pageSqlParam = sqlGenerator.generatePageSql(sqlParam.getSql());
            list = namedParameterJdbcTemplate.queryForList(pageSqlParam.getSql(), pageSqlParam.getParamMap());
        } else {
            list = namedParameterJdbcTemplate.queryForList(sqlParam.getSql(), sqlParam.getParamMap());
        }

        // 返回值是PageWrapper
        if (queryParam.isPageQuery() && PageWrapper.class.equals(method.getReturnType())) {
            SqlParam countSqlParam = sqlGenerator.generateCountSql(sqlParam.getSql());
            return this.processPageQueryResult(countSqlParam, list, queryParam);
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
        Preconditions.checkArgument(check, String.format("查询返回值必须为List或者是PageWrapper或与entityClass相同，该查询返回值为：[%s]！", method.getReturnType().getName()));
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
                return ReflectUtils.rowMapping(mapList, annotation.entityClass()).get(0);
            }

            return ReflectUtils.rowMapping(mapList, annotation.entityClass());
        }
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
    private Object processPageQueryResult(SqlParam countSqlParam, List<Map<String, Object>> mapList, QueryParam queryParam) throws Exception {
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
}
