package com.husj.dynamicdao.dao;

import com.husj.dynamicdao.annotations.BatchSave;
import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.annotations.query.Param;
import com.husj.dynamicdao.model.EntityPo;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface SaveDao {

    /**
     * 占位符方式，返回更新条数
     * @param name
     * @param sex
     * @param tel
     * @param available
     * @return
     */
    @Save("INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (?, ?, ?, ?)")
    int save1(String name, Integer sex, String tel, Boolean available);

    /**
     * 占位符并返回主键
     * @param name
     * @param sex
     * @param tel
     * @param available
     * @return
     */
    @Save(value = "INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (?, ?, ?, ?)", returnKey = true)
    int save2(String name, Integer sex, String tel, Boolean available);

    /**
     * 命名参数，返回更新条数
     * @param map
     * @return
     */
    @Save(value = "INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (:name, :sex, :tel, :available)")
    int save3(Map<String, Object> map);

    /**
     * 命名参数，返回主键
     * @param map
     * @return
     */
    @Save(value = "INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (:name, :sex, :tel, :available)", returnKey = true)
    int save4(Map<String, Object> map);

    /**
     * 命名参数第二种方式，返回更新条数
     * @param name
     * @param sex
     * @param tel
     * @param available
     * @return
     */
    @Save(value = "INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (:name, :sex, :tel, :available)")
    int save5(@Param("name") String name, @Param("sex") Integer sex, @Param("tel") String tel, @Param("available") Boolean available);

    /**
     * 命名参数，返回主键
     * @param name
     * @param sex
     * @param tel
     * @param available
     * @return
     */
    @Save(value = "INSERT INTO entity(`name_`, `sex`, `tel`, `available`) VALUES (:name, :sex, :tel, :available)", returnKey = true)
    int save6(@Param("name") String name, @Param("sex") Integer sex, @Param("tel") String tel, @Param("available") Boolean available);

    /**
     * JPA注解实体，返回更新条数
     * @param entityPo
     * @return
     */
    @Save
    int save7(EntityPo entityPo);

    /**
     * JPA注解实体，返回主键
     * @param entityPo
     * @return
     */
    @Save(returnKey = true)
    int save8(EntityPo entityPo);

    @BatchSave
    void save9(List<EntityPo> entityPos);

    @Save
    void save10(EntityPo entityPo);

}
