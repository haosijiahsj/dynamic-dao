package com.husj.dynamicdao.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author hushengjun
 * @date 2018/2/2
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    @Bean
    @Primary
    public DataSource dataSourceOne() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(DRIVER_CLASS_NAME);
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/hello-world?characterEncoding=utf-8");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");

        log.info("数据源初始化！");

        return druidDataSource;
    }

    @Bean
    public DataSource dataSourceTwo() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(DRIVER_CLASS_NAME);
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/hello-world1?characterEncoding=utf-8");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");

        log.info("数据源初始化！");

        return druidDataSource;
    }


}
