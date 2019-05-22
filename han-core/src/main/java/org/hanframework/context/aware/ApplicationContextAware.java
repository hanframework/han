package org.hanframework.context.aware;

import org.hanframework.context.ApplicationContext;

/**
 * 注入上下文
 *
 * @author liuxin
 * @version Id: ApplicationContextAware.java, v 0.1 2018/10/29 11:30 AM
 */
public interface ApplicationContextAware extends Aware {
    /**
     * 注入应用上下文
     *
     * @param applicationContext 应用上下文
     */
    void setApplicationContext(ApplicationContext applicationContext);
}
