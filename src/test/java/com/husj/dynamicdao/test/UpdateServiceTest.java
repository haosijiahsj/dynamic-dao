package com.husj.dynamicdao.test;

import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.UpdateService;
import com.husj.dynamicdao.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Slf4j
public class UpdateServiceTest {

    private UpdateService service;

    @Before
    public void setUp() {
        service = ServiceUtils.getBean(UpdateService.class);
    }

    @Test
    public void update1Test() {
        int n = service.update1("aaa", "15512345678", 1);
        log.info("{}", n);
    }

    @Test
    public void update2Test() {
        int n = service.update2("bbb", "15512345678", 2);
        log.info("{}", n);
    }

    @Test
    public void update3Test() {
        int n = service.update3(3,"ccc", "15512345678");
        log.info("{}", n);
    }

    @Test
    public void update4Test() {
        EntityPo entityPo = new EntityPo();
        entityPo.setName("ddd");
        entityPo.setSex(1);
        entityPo.setId(4);
        entityPo.setAvailable(false);

        int n = service.update4(entityPo);
        log.info("{}", n);
    }

    @Test
    public void delete1Test() {
        int n = service.delete1(1);
        log.info("{}", n);
    }

    @Test
    public void delete2Test() {
        int n = service.delete2(Arrays.asList(1, 2, 3));
        log.info("{}", n);
    }

    @Test
    public void batchUpdate1() {
        Object[] obj1 = new Object[] {"a", 6};
        Object[] obj2 = new Object[] {"b", 7};
        Object[] obj3 = new Object[] {"c", 8};

        List<Object[]> objects = Arrays.asList(obj1, obj2, obj3);

        service.batchUpdate1(objects);
    }

    @Test
    public void batchUpdate2() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "e");
        map1.put("id", 6);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "f");
        map2.put("id", 7);

        List<Map<String, Object>> objects = Arrays.asList(map1, map2);

        service.batchUpdate2(objects);
    }

}
