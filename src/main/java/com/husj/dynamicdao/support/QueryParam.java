package com.husj.dynamicdao.support;

import com.google.common.collect.Maps;
import com.husj.dynamicdao.annotations.query.Param;
import com.husj.dynamicdao.page.PageParam;
import lombok.Data;

import java.lang.annotation.Annotation;
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
    private boolean named;

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
        // 若初始化后paramMap不为空则是命名参数方式
        this.named = !this.paramMap.isEmpty();
    }

    /**
     * 没有参数
     * @return
     */
    public boolean emptyArgs() {
        return this.args.length == 0;
    }

    /**
     * 仅有一个参数
     * @return
     */
    public boolean onlyOneArg() {
        return this.args.length == 1;
    }

    /**
     * 仅有一个查询分页的参数
     * @return
     */
    public boolean onlyOnePageParamArg() {
        return this.args.length == 1 && pageParam != null;
    }

    /**
     * 返回第一个参数
     * @return
     */
    public Object firstArg() {
        return this.args[0];
    }

    /**
     * 是否是分页查询
     * @return
     */
    public boolean isPageQuery() {
        return this.pageParam != null;
    }

    /**
     * pageParam是否是最后一个参数
     * @return
     */
    public boolean pageParamIsLastArg() {
        return this.args[args.length - 1] instanceof PageParam;
    }

}
