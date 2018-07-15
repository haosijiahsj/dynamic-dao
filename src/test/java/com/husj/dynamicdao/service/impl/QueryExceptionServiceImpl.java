package com.husj.dynamicdao.service.impl;

import com.husj.dynamicdao.InjectDao;
import com.husj.dynamicdao.dao.QueryExceptionDao;
import com.husj.dynamicdao.model.EntityPo;
import com.husj.dynamicdao.service.QueryExceptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 7/15 0015.
 */
@Service
public class QueryExceptionServiceImpl implements QueryExceptionService {

    @InjectDao
    private QueryExceptionDao dao;

    @Override
    public List<Map<String, Object>> query1() {
        return dao.query1();
    }

    @Override
    public void query2() {
        dao.query2();
    }

    @Override
    public List<Enum> query3() {
        return dao.query3();
    }

    @Override
    public Enum query4() {
        return dao.query4();
    }

    @Override
    public EntityPo query5() {
        return dao.query5();
    }

    @Override
    public List<Long> query6() {
        return dao.query6();
    }

    @Override
    public Long query7() {
        return dao.query7();
    }
}
