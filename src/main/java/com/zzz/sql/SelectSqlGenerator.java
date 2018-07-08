package com.zzz.sql;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zzz.annotations.Query;
import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.support.QueryParam;
import com.zzz.support.SqlParam;
import com.zzz.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

    private static final String OFFSET = "dynamicDaoOffset";
    private static final String SIZE = "dynamicDaoSize";
    private static final String LETTER_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public SelectSqlGenerator(Method method, Query annotation, QueryParam queryParam) {
        super.method = method;
        super.annotation = annotation;
        super.queryParam = queryParam;
    }

    @Override
    public SqlParam generateSql() {
        String sql = annotation.value() + this.processConditions(method, queryParam.getParamMap());

        log.debug("生成的sql: [{}]", sql);

        return SqlParam.of(sql, queryParam.getArgs(), queryParam.getParamMap());
    }

    @Override
    public SqlParam generatePageSql(String sql) {
        int offset = queryParam.getPageParam().getSize() * (queryParam.getPageParam().getPage() - 1);

        String pageSql;
        SqlParam sqlParam = new SqlParam();
        if (queryParam.isNamed()) {
            String offsetStr = this.generateLimitStr(OFFSET, queryParam.getParamMap().keySet());
            String sizeStr = this.generateLimitStr(SIZE, queryParam.getParamMap().keySet());

            pageSql = sql + LIMIT + ":" + offsetStr + ", :" + sizeStr;

            Map<String, Object> map = Maps.newHashMap();
            queryParam.getParamMap().keySet().forEach(m -> map.put(m, queryParam.getParamMap().get(m)));

            map.put(offsetStr, offset);
            map.put(sizeStr, queryParam.getPageParam().getSize());

            sqlParam.setParamMap(map);
        } else {
            Preconditions.checkArgument(queryParam.pageParamIsLastArg(), "?占位模式下分页，PageParam参数只能在最后一位！");
            pageSql = sql + LIMIT + "?, ?";

            Object[] newArgs = new Object[queryParam.getArgs().length + 1];
            System.arraycopy(queryParam.getArgs(), 0, newArgs, 0, queryParam.getArgs().length);
            newArgs[newArgs.length - 2] = offset;
            newArgs[newArgs.length - 1] = queryParam.getPageParam().getSize();

            sqlParam.setArgs(newArgs);
        }

        log.debug("生成的分页sql: [{}]", pageSql);

        sqlParam.setSql(pageSql);

        return sqlParam;
    }

    @Override
    public SqlParam generateCountSql(String sql) {
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

        SqlParam sqlParam = new SqlParam();
        if (!queryParam.isNamed()) {
            Object[] newArgs = new Object[queryParam.getArgs().length - 1];
            System.arraycopy(queryParam.getArgs(), 0, newArgs, 0, newArgs.length);
            sqlParam.setArgs(newArgs);
        } else {
            sqlParam.setParamMap(queryParam.getParamMap());
        }
        sqlParam.setSql(sql);

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

    /**
     * 生成随机字符串
     * @param length
     * @return
     */
    private String generateRandomStr(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < length; i++) {
            sb.append(LETTER_CHAR.charAt(random.nextInt(LETTER_CHAR.length())));
        }
        return sb.toString();
    }

    /**
     * limit中两个占位符字符随机生成，避免与用户传入的冲突
     * @param str
     * @param strSet
     * @return
     */
    private String generateLimitStr(String str, Set<String> strSet) {
        while (strSet.contains(str)) {
            str = this.generateRandomStr(4) + str;
        }
        return str;
    }

}
