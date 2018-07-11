package com.husj.dynamicdao;

import java.lang.annotation.*;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectDao {
}
