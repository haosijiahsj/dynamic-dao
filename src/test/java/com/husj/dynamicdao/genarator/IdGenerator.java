package com.husj.dynamicdao.genarator;

import com.husj.dynamicdao.support.IdentifierGenerator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;

/**
 * IdGenatator
 *
 * @author shengjun.hu
 * @date 2021/6/11
 */
public class IdGenerator implements IdentifierGenerator {

    private static int a = 100001;

    @Override
    public Serializable nextKey(JdbcTemplate jdbcTemplate, Object arg) {
        return a++;
    }

}
