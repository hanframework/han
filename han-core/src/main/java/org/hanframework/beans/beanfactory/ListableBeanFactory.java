package org.hanframework.beans.beanfactory;

/**
 * 获取Bean的继承体系
 * 比如根据父类
 *
 * @author liuxin
 * @version Id: ListableBeanFactory.java, v 0.1 2018-12-06 11:05
 */
public interface ListableBeanFactory {

    /**
     * Bean名字
     *
     * @return String[]
     */
    String[] getBeanDefinitionNames();

    /**
     * 根据指定类型,查询其实现Bean
     *
     * @param beanClass bean类型
     * @return String[]
     */
    String[] getBeanNamesForType(Class<?> beanClass);
}
