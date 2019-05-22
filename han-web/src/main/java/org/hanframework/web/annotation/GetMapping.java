package org.hanframework.web.annotation;

import java.lang.annotation.*;

/**
 * @Package: org.smileframework.web.annotation
 * @Description: 绑定GET请求
 * @author: liuxin
 * @date: 2017/12/4 下午1:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface GetMapping {
    String name() default "";

    String[] value() default {};

    String[] path() default {};

    HttpMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};
}
