package org.hanframework.env.annotation;

import java.lang.annotation.*;

/**
 * 在一个应用中可能有多套配置环境。
 * 当我们在一个组件中使用@Value注解时候,默认会从已经激活的配置文件中来读取,
 * 但是也可以通过@Profile来指定。
 *
 * @author liuxin
 * @version Id: Profile.java, v 0.1 2018-12-13 16:08
 * @see Value
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Profile {
    /**
     * 激活的配置文件名
     *
     * @return 读取配置的文件名
     */
    String[] value() default "default";
}
