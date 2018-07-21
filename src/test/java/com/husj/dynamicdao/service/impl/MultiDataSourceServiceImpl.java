package com.husj.dynamicdao.service.impl;

import com.husj.dynamicdao.InjectDao;
import com.husj.dynamicdao.dao.QueryDao;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.model.Status;
import com.husj.dynamicdao.seconddao.SaveDao;
import com.husj.dynamicdao.service.MultiDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 胡胜钧
 * @date 7/21 0021.
 */
@Slf4j
@Service
public class MultiDataSourceServiceImpl implements MultiDataSourceService {

    @InjectDao
    private SaveDao saveDao;

    @InjectDao
    private QueryDao queryDao;

    @Override
    public void multiDataSourceTest() {
        EntityPo entityPo = new EntityPo();
        entityPo.setName("测试多数据源");
        entityPo.setAvailable(true);
        entityPo.setSex(1);
        entityPo.setTel("15520761820");
        entityPo.setCreateTime(LocalDateTime.now());
        entityPo.setStatus(Status.SUCCESS);

        saveDao.save1(entityPo);

        List<EntityPo> list = queryDao.query10();
        log.info("{}", list);
    }
}
