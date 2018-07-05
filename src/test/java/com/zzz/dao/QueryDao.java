package com.zzz.dao;

import com.zzz.annotations.Query;
import com.zzz.annotations.query.Condition;
import com.zzz.annotations.query.Conditions;
import com.zzz.annotations.query.Param;
import com.zzz.model.EntityPo;
import com.zzz.page.PageParam;
import com.zzz.page.PageWrapper;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface QueryDao {

    /**
     * ?占位符方式
     * @param sex
     * @return
     */
    @Query("SELECT * FROM entity e WHERE e.sex = ?")
    List<Map<String, Object>> query1(Integer sex);

    /**
     * 返回实体类
     * @param sex
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.sex = ?", entityClass = EntityPo.class)
    List<EntityPo> query2(Integer sex);

    /**
     * 命名参数
     * @param map
     * @return
     */
    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.sex = :sex", named = true)
    List<Map<String, Object>> query3(Map<String, Object> map);

    /**
     * 命名参数返回实体类
     * @param map
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.sex = :sex", named = true, entityClass = EntityPo.class)
    List<EntityPo> query4(Map<String, Object> map);

    /**
     * 动态条件查询
     * @param map
     * @return
     */
    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e", named = true)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<Map<String, Object>> query5(Map<String, Object> map);

    /**
     * 动态条件查询返回实体类
     * @param map
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<EntityPo> query6(Map<String, Object> map);

    /**
     * 命名参数单个传入
     * @param name
     * @return
     */
    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e WHERE e.name_ LIKE :name", named = true)
    List<Map<String, Object>> query7(@Param("name") String name);

    /**
     * 命名参数单个传入并返回实体类
     * @param name
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.name_ LIKE :name", named = true, entityClass = EntityPo.class)
    List<EntityPo> query8(@Param("name") String name);

    /**
     * 无参数查询
     * @return
     */
    @Query("SELECT * FROM entity")
    List<Map<String, Object>> query9();

    /**
     * 无参数查询返回实体类
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity", entityClass = EntityPo.class)
    List<EntityPo> query10();

    /**
     * 只传入分页参数
     * @param pageParam
     * @return
     */
    @Query("SELECT * FROM entity")
    List<Map<String, Object>> query10(PageParam pageParam);

    /**
     * 只传入分页参数返回实体类
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity", entityClass = EntityPo.class)
    List<EntityPo> query11(PageParam pageParam);

    /**
     * 分页动态查询
     * @param map
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e", named = true)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = 1")
    })
    List<Map<String, Object>> query12(Map<String, Object> map, PageParam pageParam);

    /**
     * 分页动态查询并返回实体类
     * @param map
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    List<EntityPo> query13(Map<String, Object> map, PageParam pageParam);

    /**
     * 分页动态查询返回条数相关信息
     * @param map
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_, sex, tel, available FROM entity e", named = true)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = 1")
    })
    PageWrapper<Map<String, Object>> query14(Map<String, Object> map, PageParam pageParam);

    /**
     * 分页动态查询返回条数相关信息
     * @param map
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    PageWrapper<EntityPo> query15(Map<String, Object> map, PageParam pageParam);

    /**
     * 返回单个实体
     * @param id
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.id = ?", entityClass = EntityPo.class)
    EntityPo query16(Integer id);

    /**
     * 查询返回了多行数据，程序报错
     * @param name
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.name_ LIKE ?", entityClass = EntityPo.class)
    EntityPo query17(String name);

    /**
     * 单个命名参数查询并分页
     * @param name
     * @param pageParam
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e", named = true, entityClass = EntityPo.class)
    @Conditions({
            @Condition("e.name_ LIKE :name"),
            @Condition("e.sex = :sex"),
            @Condition("e.available = :available")
    })
    PageWrapper<EntityPo> query18(@Param("name") String name, PageParam pageParam);

    /**
     * 使用IN关键字查询
     * @param ids
     * @return
     */
    @Query(value = "SELECT id, name_ name, sex, tel, available FROM entity e WHERE e.id IN (:ids)", named = true, entityClass = EntityPo.class)
    List<EntityPo> query19(@Param("ids") List<Integer> ids);

}
