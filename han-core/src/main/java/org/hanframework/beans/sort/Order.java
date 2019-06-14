package org.hanframework.beans.sort;

import java.lang.annotation.*;

/**
 * 根据从小到大排序
 *
 * @author liuxin
 * @version Id: Order.java, v 0.1 2019-06-13 17:06
 */
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {
    int value() default 0;
}
