package org.hanframework.web.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: PathVariable.java, v 0.1 2019-04-14 17:13
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

  String value() default "";

  boolean required() default true;
}
