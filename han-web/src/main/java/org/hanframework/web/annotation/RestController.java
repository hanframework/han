package org.hanframework.web.annotation;

import org.hanframework.beans.annotation.HanComponent;

import java.lang.annotation.*;

/**
 * @Package: org.smileframework.web.annotation
 * @Description: 控制器
 * @author: liuxin
 * @date: 2017/12/4 下午4:24
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HanComponent
public @interface RestController {
    String value() default "";
}
