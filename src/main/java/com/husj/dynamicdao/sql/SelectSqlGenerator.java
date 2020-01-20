package com.husj.dynamicdao.sql;

import com.husj.dynamicdao.annotations.Query;
import com.husj.dynamicdao.annotations.query.Condition;
import com.husj.dynamicdao.annotations.query.Conditions;
import com.husj.dynamicdao.page.PageParam;
import com.husj.dynamicdao.support.QueryParam;
import com.husj.dynamicdao.support.SqlParam;
import com.husj.dynamicdao.utils.SqlParseUtils;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class SelectSqlGenerator extends BaseSqlGenerator<Query> {

    private static final String WHERE = " WHERE";
    private static final String COLON = ":";
    private static final String LIMIT = " LIMIT ";

    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+DISTINCT\\s+(\\S+)\\s+FROM\\s+.+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_PATTERN = Pattern.compile("^.*\\s*(FROM)\\s+.+", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("\\s+(ORDER\\s+BY)\\s+[^'\"]", Pattern.CASE_INSENSITIVE);
    private static final Pattern GROUP_BY_PATTERN = Pattern.compile("\\s+(GROUP\\s+BY)\\s+[^'\"]", Pattern.CASE_INSENSITIVE);

    public SelectSqlGenerator(Method method, Query annotation, QueryParam queryParam) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
    }

    @Override
    public SqlParam generateSql() {
        boolean flag = annotation.value().toUpperCase().startsWith("SELECT");
        Assert.isTrue(flag, String.format("This SQL [%s] may be not a SELECT SQL !", annotation.value()));

        String sql = annotation.value() + this.processConditions(method, queryParam.getParamMap());

        SqlParam sqlParam;
        if (queryParam.isNamed()) {
            sqlParam = SqlParseUtils.parseNamedSql(sql, queryParam.getParamMap());
        } else {
            Object[] args = queryParam.getArgs();
            // 参数列表中去掉pageParam
            if (queryParam.isPageQuery()) {
                List<Object> newArgs = new ArrayList<>();
                for (Object arg : args) {
                    if (!(arg instanceof PageParam)) {
                        newArgs.add(arg);
                    }
                }
                args = newArgs.toArray();
            }
            sqlParam = SqlParam.of(sql, args);
        }
        if (queryParam.onlyOnePageParamArg()) {
            sqlParam.setArgs(new Object[] {});
        }

        log.debug("SQL statement [{}]", sqlParam.getSql());
        log.debug("SQL arguments {}", Arrays.toString(sqlParam.getArgs()));

        return sqlParam;
    }

    @Override
    public SqlParam generatePageSql(String sql, Object[] args) {
        int offset = queryParam.getPageParam().getSize() * (queryParam.getPageParam().getPage() - 1);

        SqlParam sqlParam = new SqlParam();
        String pageSql = sql + LIMIT + "?, ?";

        Object[] newArgs = new Object[args.length + 2];
        // 原参数列表大于0才进行复制
        if (args.length > 0) {
            System.arraycopy(args, 0, newArgs, 0, args.length);
        }
        newArgs[newArgs.length - 2] = offset;
        newArgs[newArgs.length - 1] = queryParam.getPageParam().getSize();

        sqlParam.setSql(pageSql);
        sqlParam.setArgs(newArgs);

        log.debug("SQL statement(page) [{}]", sqlParam.getSql());
        log.debug("SQL arguments(page) {}", Arrays.toString(sqlParam.getArgs()));

        return sqlParam;
    }

    @Override
    public SqlParam generateCountSql(String sql, Object[] args) {
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

        SqlParam sqlParam = SqlParam.of(sql, args);

        log.debug("SQL statement(count) [{}]", sqlParam.getSql());
        log.debug("SQL arguments(count) {}", Arrays.toString(sqlParam.getArgs()));

        return sqlParam;
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

}
