package com.husj.dynamicdao.support;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;

/**
 * IdentifierGenerator
 *
 * @author shengjun.hu
 * @date 2021/6/11
 */
public interface IdentifierGenerator {

    Serializable nextKey(JdbcTemplate jdbcTemplate, Object arg);

}
