package org.hanframework.context;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;
import org.hanframework.context.listener.ApplicationListener;
import org.hanframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * @Package: org.smileframework.ioc.bean.context
 * @Description: 配置上线文
 * @author: liuxin
 * @date: 2017/12/6 下午6:01
 */
public interface ConfigurableApplicationContext {

    /**
     * 获取可配置的
     *
     * @return
     */
    ConfigurableEnvironment getConfigurableEnvironment();

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
}
