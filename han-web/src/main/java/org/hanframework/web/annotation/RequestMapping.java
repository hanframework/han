package org.hanframework.web.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: RequestMapping.java, v 0.1 2019-03-26 17:29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestMapping {

    String name() default "";

    String[] value() default {};

    String[] path() default {};

    HttpMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};
}
