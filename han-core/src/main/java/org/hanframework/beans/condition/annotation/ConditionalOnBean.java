package org.hanframework.beans.condition.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: ConditionalOnBean.java, v 0.1 2019-01-14 16:27
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnBean {
  Class<?>[] values() default {};
  String[] names() default {};
  boolean strict() default false;
}
