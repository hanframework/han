package org.hanframework.context.aware;

/**
 * @author liuxin
 * @version Id: BeanNameAware.java, v 0.1 2019-01-29 18:35
 */
public interface BeanNameAware extends Aware {
    /**
     * 自动注入beanName
     *
     * @param name bean名称
     */
    void setBeanName(String name);
}
