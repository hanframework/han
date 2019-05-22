package org.hanframework.beans.annotation;

import java.lang.annotation.*;

/**
 * @Package: org.hanframework.beans.annotation;
 * @Description: 需要ioc扫描的组件组件
 * @author: liuxin
 * @date: 2017/11/17 下午11:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HanComponent {
    String name() default "";
}
