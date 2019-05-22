package org.hanframework.beans.condition.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: ConditionalOnMissingBean.java, v 0.1 2018/10/11 6:14 PM
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnMissingBean {
    Class<?>[] values() default {};
    String[] names() default {};
    boolean strict() default false;
}

