package org.hanframework.tool.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: A1.java, v 0.1 2019-04-18 21:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface A1 {


  String a1() default "";

  @AliasFor(value = "a1")
  String a2() default "";

}
