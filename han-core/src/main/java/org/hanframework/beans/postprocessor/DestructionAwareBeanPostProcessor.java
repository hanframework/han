package org.hanframework.beans.postprocessor;

/**
 * 提供销毁前处理器
 *
 * @author liuxin
 * @version Id: DestructionAwareBeanPostProcessor.java, v 0.1 2018/10/18 10:49 PM
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 销毁接口
     *
     * @param bean     要执行销毁方法的实例
     * @param beanName bean名字
     */
    void postProcessBeforeDestruction(Object bean, String beanName);
}
