package org.hanframework.beans.beanfactory.postprocessor;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;


/**
 * 给开发者最后一次修改BeanFactory的机会
 * @author liuxin
 * @version Id: BeanFactoryPostProcessor.java, v 0.1 2018-12-09 02:04
 */
public interface BeanFactoryPostProcessor {

  void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) ;
}
