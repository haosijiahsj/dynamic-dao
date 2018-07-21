package com.husj.dynamicdao.service;


import com.husj.dynamicdao.model.EntityPo;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
public interface MultiDataSourceService {

    void multiDataSourceTest();

    EntityPo getById(Long id);

}
