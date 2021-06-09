package com.husj.dynamicdao.test;

import com.husj.dynamicdao.model.EntityVo;
import com.husj.dynamicdao.model.Status;
import com.husj.dynamicdao.page.PageParam;
import com.husj.dynamicdao.page.PageWrapper;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.QueryService;
import com.husj.dynamicdao.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);

        List<EntityPo> list1 = service.query4(map);
        log.info("{}条数据，{}", list1.size(), list);
    }

    @Test
    public void query3Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);

        List<Map<String, Object>> list = service.query3(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query4Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);

        List<EntityPo> list = service.query4(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query5Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);
        map.put("name", "%a%");

        List<Map<String, Object>> list = service.query5(map);
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query6Test() {
        Map<String, Object> map = new HashMap<>();
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
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);
        map.put("name", "%z%");
        List<Map<String, Object>> list = service.query12(map, PageParam.of(1, 20));
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query13Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);
        map.put("name", "%z%");
        List<EntityPo> list = service.query13(map, PageParam.of(2, 2));
        log.info("{}条数据，{}", list.size(), list);
    }

    @Test
    public void query14Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);
        map.put("name", "%z%");
        PageWrapper<Map<String, Object>> pageWrapper = service.query14(map, PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query15Test() {
        Map<String, Object> map = new HashMap<>();
        map.put("sex", 1);
        map.put("name", "%z%");
        map.put("dynamicDaoSize", 20);
        PageWrapper<EntityPo> pageWrapper = service.query15(map, PageParam.of(2, 2));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query16Test() {
        EntityPo entityPo = service.query16(100);
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
        List<EntityPo> list = service.query19(Arrays.asList(72, 80, 88));
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

    @Test
    public void query22Test() {
        Set<EntityPo> list = service.query22(Arrays.asList(80, 88));
        log.info("{}", list);
    }

    @Test
    public void query23Test() {
        List<Integer> list = service.query23(Status.SUCCESS);
        log.info("{}", list);
    }

    @Test
    public void query24Test() {
        Set<Integer> set = service.query24(Status.SUCCESS);
        log.info("{}", set);
    }

    @Test
    public void query25Test() {
        List<Integer> set = service.query25(Status.SUCCESS);
        log.info("{}", set);
    }

    @Test
    public void query26Test() {
        Long i = service.query26(Status.SUCCESS);
        log.info("{}", i);
    }

    @Test
    public void query27Test() {
        List<EntityPo> entityPos = service.query27(Arrays.asList(4, 10));
        log.info("{}", entityPos);
        log.info("{}", entityPos.get(0).getName());
    }

    @Test
    public void query28Test() {
        List<Integer> list = service.query28(Arrays.asList(100, 101));
        log.info("{}", list);
    }

    @Test
    public void query29Test() {
        Map<String, Object> map = service.query29(100);
        log.info("{}", map);
    }

    @Test
    public void query30Test() {
        try {
            service.query30(100);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void query31Test() {
        PageWrapper<Integer> pageWrapper = service.query31(1, PageParam.of(1, 100));
        log.info("{}", pageWrapper);
    }

    @Test
    public void query32Test() {
        EntityVo entityVo = service.query32(100);
        log.info("{}", entityVo);
    }

    @Test
    public void query33Test() {
        List<EntityVo> entityVos = service.query33(Arrays.asList(72, 80, 88));
        log.info("{}", entityVos);
    }

}
