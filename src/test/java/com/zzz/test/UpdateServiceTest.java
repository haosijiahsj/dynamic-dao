package com.zzz.test;

import com.google.common.collect.Lists;
import com.zzz.model.EntityPo;
import com.zzz.service.UpdateService;
import com.zzz.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

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
        int n = service.delete2(Lists.newArrayList(1, 2, 3));
        log.info("{}", n);
    }

}
