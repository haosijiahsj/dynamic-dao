package com.husj.dynamicdao.utils;

import com.husj.dynamicdao.support.SqlParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hushengjun
 * @date 2019/9/20
 */
@Slf4j
public class SqlParseUtils {

    private static final Pattern NAMED_PATTERN = Pattern.compile("(:[a-zA-Z1-9]+)", Pattern.CASE_INSENSITIVE);

    private static final String COLON = ":";
    private static final String QUESTION_MARK = "?";
    private static final String COMMA = ",";

    private SqlParseUtils() {}

    /**
     * 解析具名参数sql, 返回?占位符的sql与参数数组
     * @param namedSql
     * @param paramMap
     * @return
     */
    public static SqlParam parseNamedSql(String namedSql, Map<String, Object> paramMap) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(namedSql);

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, new MapSqlParameterSource(paramMap));
        Object[] originalArgs = NamedParameterUtils.buildValueArray(parsedSql, new MapSqlParameterSource(paramMap), null);

        List<Object> argsList = new ArrayList<>();
        for (Object originalArg : originalArgs) {
            // 展开参数
            if (originalArg instanceof Collection) {
                argsList.addAll((Collection) originalArg);
            } else {
                argsList.add(originalArg);
            }
        }

        SqlParam sqlParam = new SqlParam();
        sqlParam.setSql(sqlToUse);
        sqlParam.setArgs(argsList.toArray());

        return sqlParam;
    }

    /**
     * 解析具名参数sql, 返回预编译sql与参数数组
     * @param namedSql
     * @param paramMap
     * @return
     */
    public static SqlParam parseNamedSqlByPattern(String namedSql, Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            throw new IllegalArgumentException();
        }

        Matcher namedMatcher = NAMED_PATTERN.matcher(namedSql);
        String psql = namedSql;
        List<Object> args = new ArrayList<>();
        while (namedMatcher.find()) {
            String group = namedMatcher.group();
            String name = group.replace(COLON, "");

            Object value = paramMap.get(name);
            if (value == null) {
                throw new IllegalArgumentException(String.format("具名参数：%s 未设置有效值！", name));
            }

            String partSql = QUESTION_MARK;
            if (value instanceof Collection && ((Collection) value).size() > 1) {
                int size = ((Collection) value).size();
                List<String> strings = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    strings.add(QUESTION_MARK);
                }
                partSql = StringUtils.join(COMMA + " ", strings);

                args.addAll(((Collection) value));
            } else {
                args.add(value);
            }

            psql = psql.replace(group, partSql);
        }

        log.info("{}", psql);

        return SqlParam.of(psql, args.toArray());
    }

}
