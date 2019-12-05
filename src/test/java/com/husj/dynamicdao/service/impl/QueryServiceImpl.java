package com.husj.dynamicdao.service.impl;

import com.husj.dynamicdao.DynamicDao;
import com.husj.dynamicdao.dao.QueryDao;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.model.EntityVo;
import com.husj.dynamicdao.model.Status;
import com.husj.dynamicdao.page.PageParam;
import com.husj.dynamicdao.page.PageWrapper;
import com.husj.dynamicdao.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Service
public class QueryServiceImpl implements QueryService {

    @DynamicDao
//    @Autowired
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

    @Override
    public PageWrapper<EntityVo> query20(String name, PageParam pageParam) {
        return dao.query18(name, pageParam).convert(EntityVo.class);
    }

    @Override
    public PageWrapper<EntityPo> query21(Integer sex, String name, PageParam pageParam) {
        return dao.query20(sex, name, pageParam);
    }

    @Override
    public Set<EntityPo> query22(List<Integer> ids) {
        return dao.query21(ids);
    }

    @Override
    public List<Integer> query23(Status status) {
        return dao.query23(status.name());
    }

    @Override
    public Set<Integer> query24(Status status) {
        return dao.query24(status.name());
    }

    @Override
    public List<Integer> query25(Status status) {
        return dao.query25(status.name());
    }

    @Override
    public Long query26(Status status) {
        return dao.query26(status.name());
    }

    @Override
    public List<EntityPo> query27(List<Integer> ids) {
        return dao.query27(ids);
    }

    @Override
    public List<Integer> query28(List<Integer> ids) {
        return dao.query28(ids);
    }

    @Override
    public Map<String, Object> query29(Integer id) {
        return dao.query29(id);
    }

    @Override
    public void query30(Integer id) {
        dao.query30(id);
    }

    @Override
    public PageWrapper<Integer> query31(Integer sex, PageParam pageParam) {
        return dao.query31(sex, pageParam);
    }

    @Override
    public EntityVo query32(Integer id) {
        return dao.query32(id);
    }

    @Override
    public List<EntityVo> query33(List<Integer> ids) {
        return dao.query33(ids);
    }

}
