package com.husj.dynamicdao.test;


import com.husj.dynamicdao.service.QueryExceptionService;
import com.husj.dynamicdao.utils.ServiceUtils;
import com.husj.dynamicdao.utils.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author 胡胜钧
 * @date 7/15 0015.
 */
public class QueryExceptionServiceTest {

    private QueryExceptionService service;

    @Before
    public void setUp() {
        service = ServiceUtils.getBean(QueryExceptionService.class);
    }

    @Test(expected = Exception.class)
    public void query1() {
        service.query1();
    }

    @Test(expected = Exception.class)
    public void query2() {
        service.query2();
    }

    @Test(expected = Exception.class)
    public void query3() {
        service.query3();
    }

    @Test(expected = Exception.class)
    public void query4() {
        service.query4();
    }

    @Test(expected = Exception.class)
    public void query5() {
        service.query5();
    }

    @Test(expected = Exception.class)
    public void query6() {
        service.query6();
    }

    @Test(expected = Exception.class)
    public void query7() {
        service.query7();
    }

}
