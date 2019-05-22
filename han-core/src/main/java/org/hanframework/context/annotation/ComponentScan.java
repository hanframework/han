package org.hanframework.context.annotation;

import org.hanframework.core.classpathscan.TypeFilter;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: ComponentScan.java, v 0.1 2018/10/11 6:51 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {

    String value() default "";

    /**
     * 将要扫描的包
     *
     * @return 要扫描的包
     */
    String[] scanPackages() default {};

    /**
     * 排除的包
     *
     * @return 排除过滤器
     */
    Filter[] excludeFilters() default {};


    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface Filter {

        FilterType type() default FilterType.CUSTOM;

        Class<? extends TypeFilter> []value();

        String[] pattern() default {};

    }
}
