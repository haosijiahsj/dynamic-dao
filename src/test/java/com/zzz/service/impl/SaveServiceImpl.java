package com.zzz.service.impl;

import com.google.common.collect.Maps;
import com.zzz.DynamicDao;
import com.zzz.dao.SaveDao;
import com.zzz.model.EntityPo;
import com.zzz.service.SaveService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Service
public class SaveServiceImpl implements SaveService {

    @DynamicDao
    private SaveDao dao;

    @Override
    public int save1(String name, Integer sex, String tel, Boolean available) {
        return dao.save1(name, sex, tel, available);
    }

    @Override
    public int save2(String name, Integer sex, String tel, Boolean available) {
        return dao.save2(name, sex, tel, available);
    }

    @Override
    public int save3(String name, Integer sex, String tel, Boolean available) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("sex", sex);
        map.put("tel", tel);
        map.put("available", available);

        return dao.save3(map);
    }

    @Override
    public int save4(String name, Integer sex, String tel, Boolean available) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("sex", sex);
        map.put("tel", tel);
        map.put("available", available);

        return dao.save4(map);
    }

    @Override
    public int save5(String name, Integer sex, String tel, Boolean available) {
        return dao.save5(name, sex, tel, available);
    }

    @Override
    public int save6(String name, Integer sex, String tel, Boolean available) {
        return dao.save6(name, sex, tel, available);
    }

    @Override
    public int save7(EntityPo entityPo) {
        return dao.save7(entityPo);
    }

    @Override
    public int save8(EntityPo entityPo) {
        return dao.save8(entityPo);
    }

}
