package com.zzz.test;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.zzz.model.EntityPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

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

}
