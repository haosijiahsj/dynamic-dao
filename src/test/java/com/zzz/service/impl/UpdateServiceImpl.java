package com.zzz.service.impl;

import com.google.common.collect.Maps;
import com.zzz.DynamicDao;
import com.zzz.dao.UpdateDao;
import com.zzz.model.EntityPo;
import com.zzz.service.UpdateService;
import org.springframework.stereotype.Service;

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
        Map<String, Object> map = Maps.newHashMap();
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

}
