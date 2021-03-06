package com.husj.dynamicdao.proxy;

import com.husj.dynamicdao.annotations.*;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.processor.*;
import com.husj.dynamicdao.support.InjectDaoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
@Slf4j
@SuppressWarnings("unchecked")
public class DynamicDaoInvocationHandler implements InvocationHandler {

    private JdbcTemplate jdbcTemplate;
    private InjectDaoConfiguration configuration;

    public DynamicDaoInvocationHandler(JdbcTemplate jdbcTemplate, InjectDaoConfiguration configuration) {
        this.jdbcTemplate = jdbcTemplate;
        this.configuration = configuration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation methodAnnotation : methodAnnotations) {
            Class<? extends Annotation> annotationType = methodAnnotation.annotationType();

            BaseMethodProcessor methodProcessor = null;
            if (Save.class.equals(annotationType)) {
                methodProcessor = new SaveMethodProcessor();
            } else if (Update.class.equals(annotationType)) {
                methodProcessor = new UpdateMethodProcessor();
            } else if (Query.class.equals(annotationType)) {
                methodProcessor = new QueryMethodProcessor();
            } else if (Get.class.equals(annotationType)) {
                methodProcessor = new GetMethodProcessor();
            } else if (BatchUpdate.class.equals(annotationType)) {
                methodProcessor = new BatchUpdateMethodProcessor();
            } else if (Execute.class.equals(annotationType)) {
                methodProcessor = new ExecuteMethodProcessor();
            }

            if (methodProcessor != null) {
                methodProcessor.setAnnotation(methodAnnotation);
                methodProcessor.setMethodAnnotations(methodAnnotations);
                methodProcessor.setMethod(method);
                methodProcessor.setArgs(args);
                methodProcessor.setArgsAnnotations(method.getParameterAnnotations());
                methodProcessor.setJdbcTemplate(jdbcTemplate);
                methodProcessor.setNamedParameterJdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate));
                methodProcessor.setConfiguration(configuration);

                long begin = System.currentTimeMillis();
                Object methodReturnValue = methodProcessor.process();
                long end = System.currentTimeMillis();
                log.debug("Running Time : {}ms", end - begin);

                return methodReturnValue;
            }
        }

        throw new DynamicDaoException("Dynamic dao process failed !");
    }

}
