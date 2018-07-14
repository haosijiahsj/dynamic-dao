package com.husj.dynamicdao.test;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.husj.dynamicdao.dao.QueryDao;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.SaveService;
import com.husj.dynamicdao.service.impl.QueryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 胡胜钧
 * @date 7/8 0008.
 */
@Slf4j
public class ReflectTest {

    @Test
    public void test() {
        List<Integer> integerList = Lists.newArrayList();
        List<String> stringList = Lists.newArrayList();
        List<EntityPo> entityPoList = Lists.newArrayList();

        TypeToken typeToken = TypeToken.of(integerList.getClass());
        Type clazz = ((ParameterizedType) integerList.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        ParameterizedType parameterizedType = (ParameterizedType) integerList.getClass().getGenericSuperclass();//获取当前new对象的泛型的父类类型
        Type t = parameterizedType.getActualTypeArguments()[0];
        System.out.println("clazz ==>> "+ t);

        TypeToken typeToken1 = TypeToken.of(integerList.getClass());
        TypeToken<?> genericTypeToken = typeToken1.resolveType(List.class.getTypeParameters()[0]);
        System.out.println(genericTypeToken.getType());
    }

    @Test
    public void test1() {
        log.info("{}", Number.class.isAssignableFrom(Integer.class));
        log.info("{}", Number.class.isAssignableFrom(Float.class));
        log.info("{}", Number.class.isAssignableFrom(Double.class));
        log.info("{}", Number.class.isAssignableFrom(BigDecimal.class));
        log.info("{}", Number.class.isAssignableFrom(Byte.class));

        log.info("{}", String.class.isAssignableFrom(String.class));
        log.info("{}", Character.class.isAssignableFrom(Character.class));
        log.info("{}", Boolean.class.isAssignableFrom(Boolean.class));
    }

    @Test
    public void test2() {
        ParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods = SaveService.class.getMethods();
        Arrays.stream(methods).forEach(m -> {
            String[] strs = discoverer.getParameterNames(m);
            if (strs == null) return;
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : strs) {
                stringBuilder.append(s).append(" ");
            }
            log.info("{}", stringBuilder.toString());
        });
    }

    @Test
    public void resolveTest() {
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(QueryDao.class.getMethods()[0]);
        Class<?> resolve = resolvableType.getGeneric(0).resolve();
        System.out.println(resolve);
    }

}