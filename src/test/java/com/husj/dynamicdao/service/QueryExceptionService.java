package com.husj.dynamicdao.service;


import com.husj.dynamicdao.model.EntityPo;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/15 0015.
 */
public interface QueryExceptionService {

    List<Map<String, Object>> query1();

    void query2();

    List<Enum> query3();

    Enum query4();

    EntityPo query5();

    List<Long> query6();

    Long query7();

}
