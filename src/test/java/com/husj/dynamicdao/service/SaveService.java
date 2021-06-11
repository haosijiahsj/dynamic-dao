package com.husj.dynamicdao.service;

import com.husj.dynamicdao.model.EntityPo;

import java.util.List;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface SaveService {

    int save1(String name, Integer sex, String tel, Boolean available);

    int save2(String name, Integer sex, String tel, Boolean available);

    int save3(String name, Integer sex, String tel, Boolean available);

    int save4(String name, Integer sex, String tel, Boolean available);

    int save5(String name, Integer sex, String tel, Boolean available);

    int save6(String name, Integer sex, String tel, Boolean available);

    int save7(EntityPo entityPo);

    int save8(EntityPo entityPo);

    void save9(List<EntityPo> entityPos);

}
