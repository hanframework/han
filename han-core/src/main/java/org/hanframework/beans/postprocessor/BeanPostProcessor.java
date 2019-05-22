package org.hanframework.beans.postprocessor;

import java.util.Optional;

/**
 * 初始化前后处理器接口类
 *
 * @author liuxin
 * @version Id: BeanPostProcessor.java, v 0.1 2018/10/18 8:11 PM
 */
public interface BeanPostProcessor {

    /**
     * 初始化前执行
     *
     * @param bean Bean的实例对象
     * @param beanName     当前初始化Bean的名字
     * @return Bean的实例对象
     */
    Optional<Object> postProcessBeforeInitialization(Object bean, String beanName);

    /**
     * 初始化后执行
     *
     * @param bean Bean的实例对象
     * @param beanName     当前执行初始化后置处理器的Bean的名字
     * @return Bean的实例对象
     */
    Optional<Object> postProcessAfterInitialization(Object bean, String beanName);
}
