package com.husj.dynamicdao.support;

import lombok.Builder;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

/**
 * @author hushengjun
 * @date 2019/4/26
 */
@Getter
@Builder
public class InjectDaoConfiguration {

    /**
     * 数据源
     */
    private DataSource dataSource;
    /**
     * 多数据源（key为包名）
     */
    @Builder.Default
    private Map<String, DataSource> dataSourceMap = Collections.emptyMap();
    /**
     * 谨慎进行全局配置，可能会导致column中定义的name失效
     * 若需要可使用MapperIgnore注解替代
     * 映射时忽略数据库字段的字符串如 "_", "-"
     */
    private String ignoreString;
    /**
     * 映射时忽略大小写，默认忽略
     */
    @Builder.Default
    private boolean ignoreCase = true;

}
