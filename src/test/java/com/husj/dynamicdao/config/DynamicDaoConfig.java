package com.husj.dynamicdao.config;

import com.husj.dynamicdao.AutoInjectDynamicDaoBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Slf4j
@Configuration
public class DynamicDaoConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AutoInjectDynamicDaoBean autoInjectDynamicDaoBean() {
        AutoInjectDynamicDaoBean autoInjectDynamicDaoBean = new AutoInjectDynamicDaoBean(dataSource);

        log.info("dynamic dao配置！");

        return autoInjectDynamicDaoBean;
    }

}
