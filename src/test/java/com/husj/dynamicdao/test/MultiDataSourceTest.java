package com.husj.dynamicdao.test;

import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.MultiDataSourceService;
import com.husj.dynamicdao.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
@Slf4j
public class MultiDataSourceTest {

    private MultiDataSourceService multiDataSourceService;

    @Before
    public void setUp() {
        multiDataSourceService = ServiceUtils.getBean(MultiDataSourceService.class);
    }

    @Test
    public void multiDataSourceTest() {
        multiDataSourceService.multiDataSourceTest();
    }

    @Test
    public void getByIdTest() {
        EntityPo entityPo = multiDataSourceService.getById(141L);
        log.info("{}", entityPo);
    }

}
