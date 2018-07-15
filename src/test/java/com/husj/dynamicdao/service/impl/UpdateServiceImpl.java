package com.husj.dynamicdao.service.impl;

import com.husj.dynamicdao.DynamicDao;
import com.husj.dynamicdao.dao.UpdateDao;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.UpdateService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Service
public class UpdateServiceImpl implements UpdateService {

    @DynamicDao
    private UpdateDao dao;

    @Override
    public int update1(String name, String tel, Integer id) {
        return dao.update1(name, tel, id);
    }

    @Override
    public int update2(String name, String tel, Integer id) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("tel", tel);
        map.put("id", id);
        return dao.update2(map);
    }

    @Override
    public int update3(Integer id, String name, String tel) {
        return dao.update3(id, name, tel);
    }

    @Override
    public int update4(EntityPo entityPo) {
        return dao.update4(entityPo);
    }

    @Override
    public int delete1(Integer id) {
        return dao.delete1(id);
    }

    @Override
    public int delete2(List<Integer> ids) {
        return dao.delete2(ids);
    }

    @Override
    public void batchUpdate1(List<Object[]> objects) {
        dao.batchUpdate1(objects);
    }

    @Override
    public void batchUpdate2(List<Map<String, Object>> objects) {
        dao.batchUpdate2(objects);
    }

}
