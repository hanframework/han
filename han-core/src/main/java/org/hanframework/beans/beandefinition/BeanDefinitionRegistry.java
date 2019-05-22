package org.hanframework.beans.beandefinition;

/**
 * 负责将BeanDefinition注册到BeanFactory
 * 主要给BeanFactory实现
 *
 * @author liuxin
 * @version Id: BeanDefinitionRegistry.java, v 0.1 2018/10/11 11:16 AM
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册BeanDefinition
     *
     * @param beanName       bean名字
     * @param beanDefinition bean信息
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 提供刷新已经加载的BeanDefinition信息
     *
     * @param beanName       bean名字
     * @param beanDefinition bean信息
     */
    void refreshBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据BeanName移除BeanDefinition
     *
     * @param beanName bean名字
     */
    void removeBeanDefinition(String beanName);

    /**
     * 根据名字获取BeanDefinition
     *
     * @param beanName bean名字
     * @return bean信息
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 根据名字判断是否包含有改bean
     *
     * @param beanName bean名字
     * @return true:包含
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 获取所有的BeanName集合
     *
     * @return 容器中所有的bean名字集合
     */
    String[] getBeanDefinitionNames();

    /**
     * 获取当前BeanDefinition的数量
     *
     * @return bean数量
     */
    int getBeanDefinitionCount();

}
