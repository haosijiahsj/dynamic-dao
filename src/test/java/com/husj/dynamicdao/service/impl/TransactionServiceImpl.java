package com.husj.dynamicdao.service.impl;

import com.husj.dynamicdao.DynamicDao;
import com.husj.dynamicdao.dao.SaveDao;
import com.husj.dynamicdao.dao.UpdateDao;
import com.husj.dynamicdao.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hushengjun
 * @date 2018/7/5
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TransactionServiceImpl implements TransactionService {

    @DynamicDao
    private SaveDao saveDao;

    @DynamicDao
    private UpdateDao updateDao;

    @Override
    public void save() {
        saveDao.save1("hushengjun", 1, "155", true);
        int n = 1 / 0;
        saveDao.save2("hushengjun1", 0, "155", true);
    }

}
