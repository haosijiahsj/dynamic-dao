package com.husj.dynamicdao.seconddao;

import com.husj.dynamicdao.annotations.Get;
import com.husj.dynamicdao.annotations.support.AssignDataSource;
import com.husj.dynamicdao.model.EntityPo;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
@AssignDataSource("dataSourceTwo")
public interface SecondQueryDao {

    @Get
    EntityPo getById(Long id);

}
