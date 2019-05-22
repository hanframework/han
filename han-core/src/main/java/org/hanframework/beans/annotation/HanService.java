package org.hanframework.beans.annotation;
import java.lang.annotation.*;

/**
 * @Package: pig.boot.ioc.annotation
 * @Description: 需要ioc扫描的组件组件
 * @author: liuxin
 * @date: 2017/11/17 下午11:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HanComponent
public @interface HanService {
    String name() default "";
}
