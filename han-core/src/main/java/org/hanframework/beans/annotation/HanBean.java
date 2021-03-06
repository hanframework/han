package org.hanframework.beans.annotation;

import java.lang.annotation.*;

/**
 * @Package: pig.boot.ioc.annotation
 * @Description: 指定方法上的bean
 * @author: liuxin
 * @date: 2017/11/18 下午2:06
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HanBean {
    String name() default "";

    String initMethod() default "";

    String destroyMethod() default "";
}
