package com.zzz.service;

import com.zzz.model.EntityPo;

import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface UpdateService {

    int update1(String name, String tel, Integer id);

    int update2(String name, String tel, Integer id);

    int update3(Integer id, String name, String tel);

    int update4(EntityPo entityPo);

}
