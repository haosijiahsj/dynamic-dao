package com.husj.dynamicdao.dao;

import com.husj.dynamicdao.annotations.Get;
import com.husj.dynamicdao.annotations.Query;
import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.annotations.Update;

import java.io.Serializable;
import java.util.List;

/**
 * BaseDao
 *
 * @author shengjun.hu
 * @date 2021/6/10
 */
public interface BaseDao<T> {

    @Save
    void save(T t);

    @Update
    void update(T t);

    @Get
    T get(Serializable id);

    @Query
    List<T> queryALl();

}
