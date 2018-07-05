package com.zzz.service;

import com.zzz.model.EntityPo;
import com.zzz.model.EntityVo;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface QueryService {

    List<Map<String, Object>> query1(Integer sex);

    List<EntityPo> query2(Integer sex);

    List<Map<String, Object>> query3(Map<String, Object> map);

    List<EntityPo> query4(Map<String, Object> map);

    List<Map<String, Object>> query5(Map<String, Object> map);

    List<EntityPo> query6(Map<String, Object> map);

    List<Map<String, Object>> query7(String name);

    List<EntityPo> query8(String name);

    List<Map<String, Object>> query9();

    List<EntityPo> query10();

    List<Map<String, Object>> query10(PageParam pageParam);

    List<EntityPo> query11(PageParam pageParam);

    List<Map<String, Object>> query12(Map<String, Object> map, PageParam pageParam);

    List<EntityPo> query13(Map<String, Object> map, PageParam pageParam);

    PageWrapper<Map<String, Object>> query14(Map<String, Object> map, PageParam pageParam);

    PageWrapper<EntityPo> query15(Map<String, Object> map, PageParam pageParam);

    EntityPo query16(Integer id);

    EntityPo query17(String name);

    PageWrapper<EntityPo> query18(String name, PageParam pageParam);

    List<EntityPo> query19(List<Integer> ids);

    PageWrapper<EntityVo> query20(String name, PageParam pageParam);

}
