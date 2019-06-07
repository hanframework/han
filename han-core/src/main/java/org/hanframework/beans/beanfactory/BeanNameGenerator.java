package org.hanframework.beans.beanfactory;

import org.hanframework.beans.beandefinition.BeanDefinition;

/**
 * @author liuxin
 * @version Id: BeanNameGenerator.java, v 0.1 2018/10/26 6:03 PM
 */
public interface BeanNameGenerator {
    /**
     * 生成Bean名字
     *
     * @param definition bean描述
     * @return bean名字
     */
    String generateBeanName(BeanDefinition definition);

    /**
     * 生成Bean名字
     *
     * @param beanType bean类型
     * @return bean名字
     */
    String generateBeanName(Class beanType);
}
