package org.hanframework.tool.annotation;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: AliasFor.java, v 0.1 2019-04-18 16:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {

  String value() default "";

  Class<? extends Annotation> annotation() default Annotation.class;

}
