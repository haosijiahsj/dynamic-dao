package com.zzz.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzz.model.EntityPo;
import com.zzz.model.EntityVo;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;
import com.zzz.service.QueryService;
import com.zzz.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Slf4j
public class QueryServiceTest {

    private QueryService service;

    @Before
    public void setUp() {
        service = ServiceUtils.getBean(QueryService.class);
    }

    @Test
    public void query1Test() {
        List<Map<String, Object>> list = service.query1(1);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query2Test() {
        List<EntityPo> list = service.query2(1);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query3Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);

        List<Map<String, Object>> list = service.query3(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query4Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);

        List<EntityPo> list = service.query4(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query5Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%a%");

        List<Map<String, Object>> list = service.query5(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query6Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%a%");

        List<EntityPo> list = service.query6(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query7Test() {
        List<Map<String, Object>> list = service.query7("%z%");
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query8Test() {
        List<EntityPo> list = service.query8("%z%");
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query9Test() {
        List<Map<String, Object>> list = service.query9();
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query10Test() {
        List<EntityPo> list1 = service.query10();
        List<Map<String, Object>> list2 = service.query10(PageParam.of(2, 2));
        log.info("{}条数据，{}", list1.size(), list1);
        log.info("{}条数据，{}", list2.size(), list2);
    }

    @Test
    public void query11Test() {
        List<EntityPo> list = service.query11(PageParam.of(2, 2));
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query12Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%z%");
        List<Map<String, Object>> list = service.query12(map, PageParam.of(2, 2));
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query13Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%z%");
        List<EntityPo> list = service.query13(map, PageParam.of(2, 2));
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query14Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%z%");
        PageWrapper<Map<String, Object>> pageWrapper = service.query14(map, PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query15Test() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sex", 1);
        map.put("name", "%z%");
        PageWrapper<EntityPo> pageWrapper = service.query15(map, PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query16Test() {
        EntityPo entityPo = service.query16(1);
        log.info("{}", entityPo);
    }

    @Test
    public void query17Test() {
        try {
            EntityPo entityPo = service.query17("%z%");
            log.info("{}", entityPo);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    @Test
    public void query18Test() {
        PageWrapper<EntityPo> pageWrapper = service.query18("%z%", PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query19Test() {
        List<EntityPo> list = service.query19(Lists.newArrayList(5, 6));
        log.info("{}", list);
    }

    @Test
    public void query20Test() {
        PageWrapper<EntityVo> pageWrapper = service.query20("%z%", PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query21Test() {
        PageWrapper<EntityPo> pageWrapper = service.query21(1,"%a%", PageParam.of(1, 100));
        log.info("{}", pageWrapper);
    }

}
