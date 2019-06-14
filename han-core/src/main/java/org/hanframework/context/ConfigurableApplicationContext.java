package org.hanframework.context;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;
import org.hanframework.context.listener.ApplicationListener;
import org.hanframework.env.ConfigurableEnvironment;
import org.hanframework.env.Environment;

import java.util.Map;

/**
 * @author liuxin
 * @date 2017/12/6 下午6:01
 */
public interface ConfigurableApplicationContext extends ApplicationContext{

    /**
     * 获取可配置的
     *
     * @return
     */
    Environment getEnvironment();

    /**
     * 暴露给开发者修改BeanFactory的方法
     *
     * @param postProcessor
     */
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    /**
     * 运行扩展类获取bean的副本信息
     *
     * @return
     */
    Map<String, BeanDefinition> getBeanBeanDefinitionMap();

    /**
     * 添加应用监听器
     *
     * @param listener
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 注册钩子程序
     */
    void registerShutdownHook();

    /**
     * 全局bean工厂,容器中只存在一个实例
     *
     * @return bean实例
     */
    ConfigurableBeanFactory getBeanFactory();
}
