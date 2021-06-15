package com.husj.dynamicdao.test;

import com.husj.dynamicdao.dao.QueryDao;
import com.husj.dynamicdao.service.QueryService;
import com.husj.dynamicdao.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * QueryServiceTest1
 *
 * @author hushengjun
 * @date 2021-06-15-015
 */
@Slf4j
public class QueryServiceTest1 {

    @Test
    public void queryTest() {
        QueryService queryDao = ServiceUtils.getBean(QueryService.class);
        List<Map<String, Object>> maps = queryDao.query9();
        log.info("{}", maps);
    }

}
