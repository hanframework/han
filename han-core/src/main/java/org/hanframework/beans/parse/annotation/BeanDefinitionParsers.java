package org.hanframework.beans.parse.annotation;

import org.hanframework.beans.annotation.HanComponent;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: BeanDefinitionParsers.java, v 0.1 2019-01-30 09:50
 */
@HanComponent
@Target({ ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanDefinitionParsers {
}
