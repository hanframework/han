package org.hanframework.context.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: Import.java, v 0.1 2019-01-11 18:17
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    Class<?>[] value();
}
