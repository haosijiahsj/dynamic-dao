package com.husj.dynamicdao.processor;

import com.husj.dynamicdao.annotations.Execute;

/**
 * @author 胡胜钧
 * @date 7/5 0005.
 */
public class ExecuteMethodProcessor extends BaseMethodProcessor<Execute> {

    @Override
    public Object process() throws Exception {
        jdbcTemplate.execute(annotation.value());
        return null;
    }

}
