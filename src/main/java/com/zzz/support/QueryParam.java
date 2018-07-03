package com.zzz.support;

import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.page.PageParam;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@Data
public class QueryParam {

    private PageParam pageParam;
    private Map<String, Object> paramMap;
    private Object[] args;

    public QueryParam(Object[] args) {
        this.args = args;
        this.init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        for (Object arg : args) {
            if (arg instanceof Map) {
                this.paramMap = (Map<String, Object>) arg;
            }
            if (arg instanceof PageParam) {
                this.pageParam = (PageParam) arg;
            }
        }
    }

}
