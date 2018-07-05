package com.zzz.test;

import com.zzz.model.EntityPo;
import com.zzz.model.Status;
import com.zzz.service.SaveService;
import com.zzz.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Slf4j
public class SaveServiceTest {

    private SaveService service;

    @Before
    public void setUp() {
        service = ServiceUtils.getBean(SaveService.class);
    }

    @Test
    public void save1Test() {
        int n = service.save1("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save2Test() {
        int n = service.save2("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save3Test() {
        int n = service.save3("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save4Test() {
        int n = service.save4("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save5Test() {
        int n = service.save5("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save6Test() {
        int n = service.save6("zzz", 1, "18812345678", true);
        log.info("{}", n);
    }

    @Test
    public void save7Test() {
        EntityPo entityPo = new EntityPo();
        entityPo.setName("zzz");
        entityPo.setSex(1);
        entityPo.setTel("18812345678");
        entityPo.setAvailable(false);

        int n = service.save7(entityPo);
        log.info("{}", n);
    }

    @Test
    public void save8Test() {
        EntityPo entityPo = new EntityPo();
        entityPo.setName("aaa");
        entityPo.setSex(1);
        entityPo.setTel("18812345678");
        entityPo.setAvailable(false);
        entityPo.setCreateTime(LocalDateTime.now());
        entityPo.setStatus(Status.SUCCESS);

        int n = service.save8(entityPo);
        log.info("{}", n);
    }

}
