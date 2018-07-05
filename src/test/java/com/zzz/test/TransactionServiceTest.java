package com.zzz.test;

import com.zzz.service.TransactionService;
import com.zzz.utils.ServiceUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hushengjun
 * @date 2018/7/5
 */
public class TransactionServiceTest {

    private TransactionService transactionService;

    @Before
    public void setUp() {
        transactionService = ServiceUtils.getBean(TransactionService.class);
    }

    @Test
    public void saveTest() {
        transactionService.save();
    }

}
