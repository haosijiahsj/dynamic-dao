package com.husj.dynamicdao.dao;

import com.husj.dynamicdao.annotations.Query;
import com.husj.dynamicdao.annotations.query.Condition;
import com.husj.dynamicdao.annotations.query.Conditions;
import com.husj.dynamicdao.annotations.query.MapperIgnore;
import com.husj.dynamicdao.annotations.query.Param;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.model.EntityVo;
import com.husj.dynamicdao.page.PageParam;
import com.husj.dynamicdao.page.PageWrapper;
import com.husj.dynamicdao.annotations.support.AssignDataSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@AssignDataSource("dataSourceOne")
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
    @Query("SELECT * FROM entity e WHERE e.sex = ?")
    List<EntityPo> query2(Integer sex);

    /**
     * 命名参数
     * @param map
     * @return
     */
    @Query("SELECT * FROM entity e WHERE e.sex = :sex")
    List<Map<String, Object>> query3(Map<String, Object> map);

    /**
     * 命名参数返回实体类
     * @param map
     * @return
     */
    @Query("SELECT * FROM entity e WHERE e.sex = :sex")
    List<EntityPo> query4(Map<String, Object> map);

    /**
     * 动态条件查询
     * @param map
     * @return
     */
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e WHERE e.name_ LIKE :name")
    List<Map<String, Object>> query7(@Param("name") String name);

    /**
     * 命名参数单个传入并返回实体类
     * @param name
     * @return
     */
    @Query("SELECT * FROM entity e WHERE e.name_ LIKE :name")
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
    @Query("SELECT * FROM entity")
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
    @Query("SELECT * FROM entity")
    List<EntityPo> query11(PageParam pageParam);

    /**
     * 分页动态查询
     * @param map
     * @param pageParam
     * @return
     */
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e WHERE e.id = ?")
    EntityPo query16(Integer id);

    /**
     * 查询返回了多行数据，程序报错
     * @param name
     * @return
     */
    @Query("SELECT * FROM entity e WHERE e.name_ LIKE ?")
    EntityPo query17(String name);

    /**
     * 单个命名参数查询并分页
     * @param name
     * @param pageParam
     * @return
     */
    @Query("SELECT * FROM entity e")
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
    @Query("SELECT * FROM entity e WHERE e.id IN (:ids)")
    List<EntityPo> query19(@Param("ids") List<Integer> ids);

    @Query("SELECT * FROM entity e WHERE e.sex = ? AND e.name_ LIKE ?")
    PageWrapper<EntityPo> query20(Integer sex, String name, PageParam pageParam);

    @Query("SELECT * FROM entity e WHERE e.id IN (:ids)")
    Set<EntityPo> query21(@Param("ids") List<Integer> ids);

    @Query("SELECT id FROM entity e WHERE e.status_ = :status")
    List<Integer> query23(@Param("status") String status);

    @Query("SELECT id FROM entity e WHERE e.status_ = :status")
    Set<Integer> query24(@Param("status") String status);

    @Query("SELECT id FROM entity e WHERE e.status_ = :status")
    List<Integer> query25(@Param("status") String status);

    @Query("SELECT COUNT(1) FROM entity e WHERE e.status_ = :status")
    Long query26(@Param("status") String status);

    @Query("SELECT * FROM entity e WHERE e.id IN (:ids)")
    List<EntityPo> query27(@Param("ids") List<Integer> ids);

    @Query("SELECT id FROM entity e WHERE e.id IN (:ids)")
    List<Integer> query28(@Param("ids") List<Integer> ids);

    @Query("SELECT * FROM entity e WHERE e.id = :id")
    Map<String, Object> query29(@Param("id") Integer id);

    @Query("SELECT * FROM entity e WHERE e.id = :id")
    void query30(@Param("id") Integer id);

    @Query("SELECT id FROM entity e WHERE e.sex = ?")
    PageWrapper<Integer> query31(Integer sex, PageParam pageParam);

    @MapperIgnore
    @Query("SELECT * FROM entity e WHERE e.id = ?")
    EntityVo query32(Integer id);

    @MapperIgnore
    @Query("SELECT * FROM entity e WHERE e.id IN (:ids)")
    List<EntityVo> query33(@Param("ids") List<Integer> ids);

}
