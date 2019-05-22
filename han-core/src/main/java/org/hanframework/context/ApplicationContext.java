package org.hanframework.context;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.core.Configuration;

/**
 * 最小化接口
 * 通过把接口细分,把实现细分,然后通过多继承的方式,实现
 *
 * @author: liuxin
 * @date: 2017/11/17 下午11:52
 */
public interface ApplicationContext extends ConfigurableApplicationContext {

    /**
     * 根据名字获取bean实例
     *
     * @param name bean名字
     * @return bean实例
     */
    Object getBean(String name);

    /**
     * 根据名字获取bean实例
     *
     * @param name         bean名字
     * @param requiredType bean类型
     * @param <T>          bean类型
     * @return bean实例
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 根据bean类型获取bean名字,从而获取bean实例
     *
     * @param beanClass bean类型
     * @param <T>       bean类型
     * @return bean实例
     */
    <T> T getBean(Class<T> beanClass);

    /**
     * 是否包含bean
     *
     * @param name bean名字
     * @return bean实例
     */
    boolean containsBean(String name);

    /**
     * 全局bean工厂,容器中只存在一个实例
     *
     * @return bean实例
     */
    ConfigurableBeanFactory getBeanFactory();

    /**
     * 容器中集中化配置
     *
     * @return 全局配置
     */
    Configuration getConfiguration();
}
