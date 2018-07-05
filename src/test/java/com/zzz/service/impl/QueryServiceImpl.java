package com.zzz.service.impl;

import com.zzz.DynamicDao;
import com.zzz.dao.QueryDao;
import com.zzz.model.EntityPo;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;
import com.zzz.service.QueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Service
public class QueryServiceImpl implements QueryService {

    @DynamicDao
    private QueryDao dao;

    @Override
    public List<Map<String, Object>> query1(Integer sex) {
        return dao.query1(sex);
    }

    @Override
    public List<EntityPo> query2(Integer sex) {
        return dao.query2(sex);
    }

    @Override
    public List<Map<String, Object>> query3(Map<String, Object> map) {
        return dao.query3(map);
    }

    @Override
    public List<EntityPo> query4(Map<String, Object> map) {
        return dao.query4(map);
    }

    @Override
    public List<Map<String, Object>> query5(Map<String, Object> map) {
        return dao.query5(map);
    }

    @Override
    public List<EntityPo> query6(Map<String, Object> map) {
        return dao.query6(map);
    }

    @Override
    public List<Map<String, Object>> query7(String name) {
        return dao.query7(name);
    }

    @Override
    public List<EntityPo> query8(String name) {
        return dao.query8(name);
    }

    @Override
    public List<Map<String, Object>> query9() {
        return dao.query9();
    }

    @Override
    public List<EntityPo> query10() {
        return dao.query10();
    }

    @Override
    public List<Map<String, Object>> query10(PageParam pageParam) {
        return dao.query10(pageParam);
    }

    @Override
    public List<EntityPo> query11(PageParam pageParam) {
        return dao.query11(pageParam);
    }

    @Override
    public List<Map<String, Object>> query12(Map<String, Object> map, PageParam pageParam) {
        return dao.query12(map, pageParam);
    }

    @Override
    public List<EntityPo> query13(Map<String, Object> map, PageParam pageParam) {
        return dao.query13(map, pageParam);
    }

    @Override
    public PageWrapper<Map<String, Object>> query14(Map<String, Object> map, PageParam pageParam) {
        return dao.query14(map, pageParam);
    }

    @Override
    public PageWrapper<EntityPo> query15(Map<String, Object> map, PageParam pageParam) {
        return dao.query15(map, pageParam);
    }

    @Override
    public EntityPo query16(Integer id) {
        return dao.query16(id);
    }

    @Override
    public EntityPo query17(String name) {
        return dao.query17(name);
    }

    @Override
    public PageWrapper<EntityPo> query18(String name, PageParam pageParam) {
        return dao.query18(name, pageParam);
    }

    @Override
    public List<EntityPo> query19(List<Integer> ids) {
        return dao.query19(ids);
    }

}
