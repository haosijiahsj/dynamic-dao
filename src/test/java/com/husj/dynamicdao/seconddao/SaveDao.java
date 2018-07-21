package com.husj.dynamicdao.seconddao;

import com.husj.dynamicdao.annotations.Save;
import com.husj.dynamicdao.annotations.query.Param;
import com.husj.dynamicdao.model.EntityPo;

import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
public interface SaveDao {

    /**
     * JPA注解实体，返回更新条数
     * @param entityPo
     * @return
     */
    @Save
    int save1(EntityPo entityPo);

}
