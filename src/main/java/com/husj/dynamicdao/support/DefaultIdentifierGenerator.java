package com.husj.dynamicdao.support;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;

/**
 * DefaultIdentifierGenerator
 *
 * @author hushengjun
 * @date 2021-06-12-012
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable nextKey(JdbcTemplate jdbcTemplate, Object arg) {
        return null;
    }

}
