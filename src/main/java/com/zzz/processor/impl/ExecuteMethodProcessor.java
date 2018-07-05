package com.zzz.processor.impl;

import com.zzz.annotations.Execute;
import com.zzz.processor.BaseMethodProcessor;

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
