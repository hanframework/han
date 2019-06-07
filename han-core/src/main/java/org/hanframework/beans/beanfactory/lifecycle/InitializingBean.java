package org.hanframework.beans.beanfactory.lifecycle;

/**
 * @author liuxin
 * @version Id: InitializingBean.java, v 0.1 2018-12-04 15:32
 */
public interface InitializingBean {
    /**
     * 初始化方法
     */
    void afterPropertiesSet();
}
