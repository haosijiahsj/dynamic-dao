package com.husj.dynamicdao.test;

import com.husj.dynamicdao.service.MultiDataSourceService;
import com.husj.dynamicdao.utils.ServiceUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
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

}
