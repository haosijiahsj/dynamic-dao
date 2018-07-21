package com.husj.dynamicdao.config;

import com.husj.dynamicdao.AutoInjectDynamicDaoBean;
import com.husj.dynamicdao.support.MultiDataSourceScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
    public AutoInjectDynamicDaoBean autoInjectDynamicDaoBean() {
        AutoInjectDynamicDaoBean autoInjectDynamicDaoBean = new AutoInjectDynamicDaoBean();
        autoInjectDynamicDaoBean.setDataSource(dataSourceOne);

        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("com.husj.dynamicdao.dao", dataSourceOne);
        dataSourceMap.put("com.husj.dynamicdao.seconddao", dataSourceTwo);
        autoInjectDynamicDaoBean.setDataSourceMap(dataSourceMap);
        autoInjectDynamicDaoBean.setScope(MultiDataSourceScope.PACKAGE);

        log.info("dynamic dao配置！");

        return autoInjectDynamicDaoBean;
    }

}
