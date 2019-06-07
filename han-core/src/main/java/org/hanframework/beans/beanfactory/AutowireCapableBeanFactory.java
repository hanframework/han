package org.hanframework.beans.beanfactory;

import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.exception.BeanCreationException;
import org.hanframework.beans.postprocessor.impl.DependencyDescriptor;

import java.util.Set;

/**
 * 提供根据注入依赖的方法
 *
 * @author liuxin
 * @version Id: AutowireCapableBeanFactory.java, v 0.1 2018/10/18 4:17 PM
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 根据名字注入
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * 根据类型注入
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * 根据构造注入
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * 根据bean类型获取实例
     *
     * @param beanType bean类型
     * @param <T>      泛型
     * @return bean实例
     * @throws BeanCreationException 创建异常
     */
    <T> T createBean(Class<T> beanType) throws BeanCreationException;

    /**
     * 根据名字获取依赖
     *
     * @param beanName bean名字
     * @return bean实例
     * @throws Exception 异常
     */
    Object resolveDependency(String beanName) throws Exception;

    /**
     * 根据名字获取依赖
     *
     * @param beanType bean类型
     * @return bean实例
     * @throws Exception 异常
     */
    Object resolveDependency(Class<?> beanType) throws Exception;


    /**
     * 获取依赖
     *
     * @param descriptor    依赖描述
     * @param typeConverter 类型转换
     * @return bean实例
     */
    Object resolveDependency(DependencyDescriptor descriptor,
                             TypeConverter typeConverter);
    //-------------------------------------------------------------------------
    // Specialized methods for fine-grained control over the bean lifecycle
    //-------------------------------------------------------------------------

    /**
     * 初始化bean
     *
     * @param existingBean bean实例
     * @param beanName     bean名字
     * @param mbd          bean描述
     * @return 初始化后的bean实例
     */
    Object initializeBean(Object existingBean, String beanName, GenericBeanDefinition mbd);


    /**
     * 统一执行初始化前处理器
     *
     * @param existingBean bean实例
     * @param beanName     bean名字
     * @return 经过初始化前置处理的bean实例
     * @throws BeanCreationException 异常
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeanCreationException;

    /**
     * 统一执行初始化后处理器
     *
     * @param existingBean bean实例
     * @param beanName     bean名字
     * @return 经过初始化后置处理的bean实例
     * @throws BeanCreationException 异常
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeanCreationException;


}
