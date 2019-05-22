package org.hanframework.context.aware;
import org.hanframework.core.env.Environment;

/**
 * 注入环境
 *
 * @author liuxin
 * @version Id: EnvironmentAware.java, v 0.1 2018/10/29 11:29 AM
 */
public interface EnvironmentAware extends Aware {

    /**
     * 自动注入环境信息
     *
     * @param environment 环境
     */
    void setEnvironment(Environment environment);
}
