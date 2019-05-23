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


    <T> T createBean(Class<T> beanClass) throws BeanCreationException;


    Object resolveDependency(String beanName) throws Exception;

    Object resolveDependency(Class<?> beanType) throws Exception;


    //-------------------------------------------------------------------------
    // Specialized methods for fine-grained control over the bean lifecycle
    //-------------------------------------------------------------------------


    Object initializeBean(Object existingBean, String beanName, GenericBeanDefinition mbd);


    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeanCreationException;


    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeanCreationException;


    Object resolveDependency(DependencyDescriptor descriptor, String beanName,
                             Set<String> autowiredBeanNames, TypeConverter typeConverter);


}
