package com.zzz.service.impl;

import com.zzz.DynamicDao;
import com.zzz.dao.SaveDao;
import com.zzz.dao.UpdateDao;
import com.zzz.service.TransactionService;
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
