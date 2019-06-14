package org.hanframework.boot;

import org.hanframework.autoconfigure.EnableAutoConfiguration;
import org.hanframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: HanBootstrapClass.java, v 0.1 2019-06-13 23:34
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan
@EnableAutoConfiguration
public @interface HanBootstrapClass {
}
