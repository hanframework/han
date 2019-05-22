package org.hanframework.web.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface DeleteMapping {
  String name() default "";

  String[] value() default {};

  String[] path() default {};

  HttpMethod[] method() default {};

  String[] params() default {};

  String[] headers() default {};

  String[] consumes() default {};

  String[] produces() default {};
}
