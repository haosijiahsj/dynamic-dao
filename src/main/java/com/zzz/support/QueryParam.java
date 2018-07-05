package com.zzz.support;

import com.google.common.collect.Maps;
import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.annotations.query.Param;
import com.zzz.page.PageParam;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
@Data
@SuppressWarnings("unchecked")
public class QueryParam {

    private PageParam pageParam;
    private Map<String, Object> paramMap;
    private Object[] args;
    private Annotation[][] argsAnnotations;

    public QueryParam(Object[] args, Annotation[][] argsAnnotations) {
        if (args == null) {
            this.args = new Object[] {};
        } else {
            this.args = args;
        }
        this.paramMap = Maps.newHashMap();
        this.argsAnnotations = argsAnnotations;
        this.init();
    }

    /**
     * 初始化查询参数
     */
    private void init() {
        for (int i = 0; i < args.length; i++) {
            Annotation[] argAnnotations = argsAnnotations[i];
            for (Annotation argAnnotation : argAnnotations) {
                if (Param.class.equals(argAnnotation.annotationType())) {
                    this.paramMap.put(((Param) argAnnotation).value(), args[i]);
                }
            }

            if (args[i] instanceof Map) {
                this.paramMap.putAll((Map<String, Object>) args[i]);
            }
            if (args[i] instanceof PageParam) {
                this.pageParam = (PageParam) args[i];
            }
        }
    }

}
