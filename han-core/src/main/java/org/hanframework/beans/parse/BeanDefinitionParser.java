package org.hanframework.beans.parse;

import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;

/**
 * BeanDefinition的一个解析器入口
 * @author liuxin
 * @version Id: BeanDefinitionParse.java, v 0.1 2018/10/26 5:42 PM
 */
public interface BeanDefinitionParser{

    /**
     * 加载解析到的BeanDefinition到BeanFactory
     * @param beanFactory bean工厂
     */
    void loadBeanDefinitionParse(DefaultListableBeanFactory beanFactory);

}
