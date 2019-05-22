package org.hanframework.beans.condition.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: AutoConfigureAfter.java, v 0.1 2019-01-29 10:23
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoConfigureAfter {
    Class<?>[] values() default {};

    String[] names() default {};

    boolean strict() default false;
}
