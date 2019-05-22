package org.hanframework.autoconfigure;

import java.lang.annotation.*;

/**
 * 当标记由此注解则意味着中从HanFactoriesLoader中去读取自动化配置的
 *
 * @see org.hanframework.tool.extension.HanFactoriesLoader
 * @author liuxin
 * @version Id: EnableAutoConfiguration.java, v 0.1 2019-01-14 17:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoConfiguration {
}
