package com.husj.dynamicdao.processor;

import lombok.Setter;
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
@Setter
public abstract class BaseMethodProcessor<T extends Annotation> {

    protected static final Logger log = LoggerFactory.getLogger(BaseMethodProcessor.class);

    protected T annotation;
    protected Annotation[] methodAnnotations;
    protected Method method;
    protected Object[] args;
    protected Annotation[][] argsAnnotations;
    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public abstract Object process() throws Exception;

}
