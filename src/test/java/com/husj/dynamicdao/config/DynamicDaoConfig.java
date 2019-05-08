package com.husj.dynamicdao.config;

import com.husj.dynamicdao.InjectDaoBeanPostProcessor;
import com.husj.dynamicdao.support.InjectDaoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("dataSourceOne")
    private DataSource dataSourceOne;

    @Autowired
    @Qualifier("dataSourceTwo")
    private DataSource dataSourceTwo;

    @Bean
    public InjectDaoBeanPostProcessor injectDaoBean() {
        return new InjectDaoBeanPostProcessor();
    }

}
