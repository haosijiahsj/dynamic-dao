package com.husj.dynamicdao.spring.support;

import com.husj.dynamicdao.support.InjectDaoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * DynamicDaoSupport
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
@Slf4j
public abstract class DynamicDaoSupport {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private InjectDaoConfiguration configuration;

    public void setDataSource(DataSource dataSource) {
        if (this.dataSource == null) {
            this.dataSource = dataSource;
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    public void setConfiguration(InjectDaoConfiguration configuration) {
        this.configuration = configuration;
    }

    public InjectDaoConfiguration getConfiguration() {
        return configuration;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
