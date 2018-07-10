package com.husj.dynamicdao.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author hushengjun
 * @date 2018/7/4
 */
@Slf4j
@Configuration
public class TransactionConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceTransactionManager transactionManager() {
        log.info("初始化事务配置！");
        return new DataSourceTransactionManager(dataSource);
    }

}
