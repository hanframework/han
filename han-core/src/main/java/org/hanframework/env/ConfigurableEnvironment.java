package org.hanframework.env;

import org.hanframework.env.resolver.source.MutablePropertySources;

/**
 * 为什么定义两个接口:
 * 1.Environment
 * 只提供应用环境的方法
 * 2.ConfigurableEnvironment
 * 实现ConfigurableEnvironment接口就可以对环境进行配置
 *
 * @author liuxin
 * @version Id: ConfigurableEnvironment.java, v 0.1 2018/10/16 7:44 PM
 */
public interface ConfigurableEnvironment extends Environment {

    /**
     * 清空并重新设置激活的配置文件
     *
     * @param profiles 激活的配置文件
     */
    void setActiveProfiles(String... profiles);

    /**
     * 标记激活的配置文件
     *
     * @param profile 激活的配置文件
     */
    void addActiveProfile(String profile);

    /**
     * 清空并重新设置默认配置文件
     *
     * @param profiles 运行的配置文件
     */
    void setDefaultProfiles(String... profiles);

    /**
     * 多配置资源
     *
     * @return MutablePropertySources
     */
    MutablePropertySources getPropertySources();


}
