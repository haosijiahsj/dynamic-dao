package com.husj.dynamicdao.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
public abstract class BaseMethodProcessor<T extends Annotation> {

    protected static final Logger log = LoggerFactory.getLogger(BaseMethodProcessor.class);

    protected T annotation;
    protected Annotation[] methodAnnotations;
    protected Method method;
    protected Object[] args;
    protected Annotation[][] argsAnnotations;
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setAnnotation(T annotation) {
        this.annotation = annotation;
    }

    public void setMethodAnnotations(Annotation[] methodAnnotations) {
        this.methodAnnotations = methodAnnotations;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setArgsAnnotations(Annotation[][] argsAnnotations) {
        this.argsAnnotations = argsAnnotations;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public abstract Object process() throws Exception;

}