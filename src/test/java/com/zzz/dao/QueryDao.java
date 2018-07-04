package com.zzz.dao;

import com.zzz.annotations.Query;
import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.annotations.query.Param;
import com.zzz.model.EntityPo;
import com.zzz.page.PageParam;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface QueryDao {

    @Query("SELECT * FROM entity e WHERE e.sex = ?")
    List<Map<String, Object>> query1(Integer sex);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.sex = ?", entityClass = EntityPo.class)
    List<EntityPo> query2(Integer sex);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.sex = :sex", named = true)
    List<Map<String, Object>> query3(Map<String, Object> map);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.sex = :sex", named = true, entityClass = EntityPo.class)
    List<Map<String, Object>> query4(Map<String, Object> map);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e", named = true)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<Map<String, Object>> query5(Map<String, Object> map);

    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<EntityPo> query6(Map<String, Object> map);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.name LIKE :name", named = true)
    List<Map<String, Object>> query7(@Param("name") String name);

    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.name LIKE :name", named = true, entityClass = EntityPo.class)
    List<EntityPo> query8(@Param("name") String name);

    @Query("SELECT * FROM entity")
    List<Map<String, Object>> query9();

    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity", entityClass = EntityPo.class)
    List<EntityPo> query10();

    @Query("SELECT * FROM entity")
    List<Map<String, Object>> query10(PageParam pageParam);

    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity", entityClass = EntityPo.class)
    List<EntityPo> query11(PageParam pageParam);

    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e", named = true)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<Map<String, Object>> query12(Map<String, Object> map, PageParam pageParam);

    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<EntityPo> query13(Map<String, Object> map, PageParam pageParam);

}
