package org.hanframework.web.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: PatchMapping.java, v 0.1 2019-04-14 17:12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface PatchMapping {
  String name() default "";

  String[] value() default {};

  String[] path() default {};

  HttpMethod[] method() default {};

  String[] params() default {};

  String[] headers() default {};

  String[] consumes() default {};

  String[] produces() default {};
}
