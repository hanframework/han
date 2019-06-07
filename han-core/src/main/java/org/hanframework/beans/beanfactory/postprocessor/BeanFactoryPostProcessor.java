package org.hanframework.beans.beanfactory.postprocessor;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;


/**
 * 给开发者最后一次修改BeanFactory的机会。
 * BeanFactory是生成Bean的工厂,最简单的应用就是我们可以注册新的Bean条件拦截器和类型转换器
 * 条件拦截器和类型转换器
 *
 * @author liuxin
 * @version Id: BeanFactoryPostProcessor.java, v 0.1 2018-12-09 02:04
 */
public interface BeanFactoryPostProcessor {

    /**
     * bean工厂后置处理
     *
     * @param beanFactory bean工厂
     */
    void postProcessBeanFactory(ConfigurableBeanFactory beanFactory);
}
