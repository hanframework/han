package org.hanframework.context.aware;

import org.hanframework.beans.beanfactory.BeanFactory;

/**
 * 自动注入BeanFactory
 *
 * @author liuxin
 * @version Id: BeanFactoryAware.java, v 0.1 2018/11/19 3:07 PM
 */
public interface BeanFactoryAware extends Aware {
    /**
     * 注入工厂实例
     *
     * @param beanFactory 工厂实例
     */
    void setBeanFactory(BeanFactory beanFactory);
}
