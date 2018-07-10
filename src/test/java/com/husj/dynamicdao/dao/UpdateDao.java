package com.husj.dynamicdao.dao;

import com.husj.dynamicdao.annotations.BatchUpdate;
import com.husj.dynamicdao.annotations.Update;
import com.husj.dynamicdao.annotations.query.Param;
import com.husj.dynamicdao.model.EntityPo;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface UpdateDao {

    /**
     * 占位符方式，返回更新条数
     * @param name
     * @param tel
     * @param id
     * @return
     */
    @Update("UPDATE entity e SET e.`name_` = ?, e.tel = ? WHERE e.id = ?")
    int update1(String name, String tel, Integer id);

    /**
     * 命名参数方式，返回更新条数
     * @param map
     * @return
     */
    @Update(value = "UPDATE entity e SET e.`name_` = :name, e.tel = :tel WHERE e.id = :id")
    int update2(Map<String, Object> map);

    /**
     * 命名参数方式，返回更新条数
     * @param id
     * @param name
     * @param tel
     * @return
     */
    @Update(value = "UPDATE entity e SET e.`name_` = :name, e.tel = :tel WHERE e.id = :id")
    int update3(@Param("id") Integer id, @Param("name") String name, @Param("tel") String tel);

    /**
     * JPA注解实体，返回更新条数，字段值为null的将会更新为null
     * @param entityPo
     * @return
     */
    @Update
    int update4(EntityPo entityPo);

    /**
     * 删除一条数据
     * @param id
     * @return
     */
    @Update(value = "DELETE FROM entity WHERE id = ?")
    int delete1(int id);

    /**
     * 删除多条数据
     * @param ids
     * @return
     */
    @Update(value = "DELETE FROM entity WHERE id IN (:ids)")
    int delete2(@Param("ids") List<Integer> ids);

    @BatchUpdate("UPDATE entity e SET e.name_ = ? where e.id = ?")
    void batchUpdate1(List<Object[]> objects);

    @BatchUpdate(value = "UPDATE entity e SET e.name_ = :name where e.id = :id", named = true)
    void batchUpdate2(List<Map<String, Object>> objects);

}
