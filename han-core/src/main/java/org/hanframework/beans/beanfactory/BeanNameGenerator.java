package org.hanframework.beans.beanfactory;

import org.hanframework.beans.beandefinition.BeanDefinition;

/**
 * @author liuxin
 * @version Id: BeanNameGenerator.java, v 0.1 2018/10/26 6:03 PM
 */
public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition definition);

    String generateBeanName(Class cls);
}
