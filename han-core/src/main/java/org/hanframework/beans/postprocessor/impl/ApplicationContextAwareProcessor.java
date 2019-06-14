package org.hanframework.beans.postprocessor.impl;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.context.AbstractApplicationContext;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.context.aware.*;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 系统内置的处理器
 * ApplicationContextAwareProcessor
 * 应用上下文处理器只有一个目的就是对所有继承Aware接口的Bean,进行注入Aware对象
 *
 * @author liuxin
 * @version Id: ApplicationContextAwareProcessor.java, v 0.1 2018/10/29 11:32 AM
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private AbstractApplicationContext applicationContext;

    private ConfigurableBeanFactory beanFactory;

    public ApplicationContextAwareProcessor(AbstractApplicationContext applicationContext, ConfigurableBeanFactory beanFactory) {
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
    }

    /**
     * 在里面对实现Aware接口进行注册
     *
     * @param bean     应该是Bean的实例
     * @param beanName bean名称
     */
    @Override
    public Optional<Object> postProcessBeforeInitialization(Object bean, String beanName) {
        Optional<Object> beanOptional = Optional.ofNullable(bean);
        beanOptional.ifPresent(x -> invokeAwareInterfaces(x, beanName));
        return beanOptional;
    }

    @Override
    public Optional<Object> postProcessAfterInitialization(Object existingBean, String beanName) {
        return Optional.ofNullable(existingBean);
    }


    private void invokeAwareInterfaces(Object bean, String beanName) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory((BeanFactory) this.beanFactory);
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof ConfigurationAware) {
                ((ConfigurationAware) bean).setConfiguration(beanFactory.getConfiguration());
            }
        }
    }
}
