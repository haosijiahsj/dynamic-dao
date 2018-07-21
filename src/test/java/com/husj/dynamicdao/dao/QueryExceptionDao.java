package com.husj.dynamicdao.dao;

import com.husj.dynamicdao.annotations.Query;
import com.husj.dynamicdao.model.EntityPo;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/15 0015.
 */
public interface QueryExceptionDao {

    @Query("")
    List<Map<String, Object>> query1();

    @Query("SELECT * FROM entity e")
    void query2();

    @Query("SELECT * FROM entity e")
    List<Enum> query3();

    @Query("SELECT * FROM entity e WHERE e.id = 123")
    Enum query4();

    @Query("SELECT * FROM entity e")
    EntityPo query5();

    @Query("SELECT * FROM entity e")
    List<Long> query6();

    @Query("SELECT * FROM entity e")
    Long query7();

}
