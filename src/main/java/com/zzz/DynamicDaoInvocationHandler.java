package com.zzz;

import com.zzz.annotations.BatchUpdate;
import com.zzz.annotations.Query;
import com.zzz.annotations.Save;
import com.zzz.annotations.Update;
import com.zzz.processor.BaseMethodProcessor;
import com.zzz.processor.impl.BatchUpdateMethodProcessor;
import com.zzz.processor.impl.QueryMethodProcessor;
import com.zzz.processor.impl.SaveMethodProcessor;
import com.zzz.processor.impl.UpdateMethodProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
@SuppressWarnings("unchecked")
public class DynamicDaoInvocationHandler implements InvocationHandler {

    private JdbcTemplate jdbcTemplate;

    public DynamicDaoInvocationHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            } else if (BatchUpdate.class.equals(annotationType)) {
                methodProcessor = new BatchUpdateMethodProcessor();
            }

            if (methodProcessor != null) {
                methodProcessor.setAnnotation(methodAnnotation);
                methodProcessor.setMethod(method);
                methodProcessor.setArgs(args);
                methodProcessor.setArgsAnnotations(method.getParameterAnnotations());
                methodProcessor.setJdbcTemplate(jdbcTemplate);

                return methodProcessor.process();
            }
        }

        throw new IllegalArgumentException("dynamic dao代理执行失败！");
    }

}
